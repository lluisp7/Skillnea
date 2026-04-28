package com.skillnea.mobile.core.network

import com.skillnea.mobile.core.config.ApiConfig
import com.skillnea.mobile.core.model.SubmissionReceipt
import com.skillnea.mobile.core.model.SurveyAnswer
import com.skillnea.mobile.core.model.SurveyOption
import com.skillnea.mobile.core.model.SurveyQuestion
import com.skillnea.mobile.core.model.SurveySubmission
import com.skillnea.mobile.core.model.TestSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class AppsScriptApiClient(
    private val config: ApiConfig,
) {
    val isEnabled: Boolean
        get() = config.isRemoteEnabled

    suspend fun fetchTests(): List<TestSummary> = withContext(Dispatchers.IO) {
        val payload = getJson(config.buildUrl(action = "tests"))
        ensureOk(payload)
        parseTests(payload.optJSONArray("data"))
    }

    suspend fun fetchQuestions(testId: String): List<SurveyQuestion> = withContext(Dispatchers.IO) {
        val payload = getJson(config.buildUrl(action = "questions", queryParams = mapOf("testId" to testId)))
        ensureOk(payload)
        parseQuestions(payload.optJSONArray("data"))
    }

    suspend fun submitSurvey(submission: SurveySubmission): SubmissionReceipt = withContext(Dispatchers.IO) {
        val payload = postJson(
            url = config.buildUrl(action = "submit"),
            body = submissionToJson(submission),
        )
        val status = payload.optString("status", "error")
        SubmissionReceipt(
            status = status,
            message = payload.optString("message", "Submission completed"),
            submissionId = payload.optString("submissionId", "n/a"),
        )
    }

    private fun ensureOk(payload: JSONObject) {
        val status = payload.optString("status", "error")
        require(status == "ok") {
            payload.optString("message", "Apps Script returned an unexpected response")
        }
    }

    private fun parseTests(data: JSONArray?): List<TestSummary> {
        if (data == null) return emptyList()
        return buildList {
            for (index in 0 until data.length()) {
                val item = data.getJSONObject(index)
                add(
                    TestSummary(
                        id = item.optString("id"),
                        title = item.optString("title"),
                        description = item.optString("description"),
                        estimatedMinutes = item.optInt("estimatedMinutes"),
                        category = item.optString("category"),
                        questionCount = item.optInt("questionCount"),
                        active = item.optBoolean("active", true),
                    ),
                )
            }
        }
    }

    private fun parseQuestions(data: JSONArray?): List<SurveyQuestion> {
        if (data == null) return emptyList()
        return buildList {
            for (index in 0 until data.length()) {
                val item = data.getJSONObject(index)
                val options = mutableListOf<SurveyOption>()
                val rawOptions = item.optJSONArray("options") ?: JSONArray()
                for (optionIndex in 0 until rawOptions.length()) {
                    val option = rawOptions.getJSONObject(optionIndex)
                    options.add(
                        SurveyOption(
                            id = option.optString("id"),
                            label = option.optString("label"),
                            value = option.optInt("value"),
                        ),
                    )
                }
                add(
                    SurveyQuestion(
                        id = item.optString("id"),
                        testId = item.optString("testId"),
                        order = item.optInt("order"),
                        prompt = item.optString("prompt"),
                        helper = item.optString("helper"),
                        dimension = item.optString("dimension"),
                        options = options,
                    ),
                )
            }
        }
    }

    private fun submissionToJson(submission: SurveySubmission): String {
        val answers = JSONArray()
        submission.answers.forEach { answer: SurveyAnswer ->
            answers.put(
                JSONObject()
                    .put("questionId", answer.questionId)
                    .put("optionId", answer.optionId)
                    .put("score", answer.score),
            )
        }

        return JSONObject()
            .put("participantEmail", submission.participantEmail)
            .put("testId", submission.testId)
            .put("submittedAt", submission.submittedAt)
            .put("answers", answers)
            .toString()
    }

    private fun getJson(url: String): JSONObject {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 10_000
            readTimeout = 10_000
        }
        return connection.useAndReadJson()
    }

    private fun postJson(url: String, body: String): JSONObject {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 10_000
            readTimeout = 10_000
            doOutput = true
            setRequestProperty("Content-Type", "application/json; charset=utf-8")
        }

        OutputStreamWriter(connection.outputStream).use { writer ->
            writer.write(body)
        }
        return connection.useAndReadJson()
    }

    private fun HttpURLConnection.useAndReadJson(): JSONObject {
        return try {
            val stream = if (responseCode in 200..299) inputStream else errorStream
            val raw = BufferedReader(InputStreamReader(stream)).use { it.readText() }
            JSONObject(raw)
        } finally {
            disconnect()
        }
    }
}
