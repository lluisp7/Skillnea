package com.skillnea.mobile.data.remote

import com.skillnea.mobile.config.ApiConfig
import com.skillnea.mobile.data.remote.dto.toDomain
import com.skillnea.mobile.data.remote.dto.toRequestDto
import com.skillnea.mobile.model.SubmissionReceipt
import com.skillnea.mobile.model.SurveyQuestion
import com.skillnea.mobile.model.SurveySubmission
import com.skillnea.mobile.model.TestSummary

class AppsScriptApiClient(
    private val config: ApiConfig,
    private val service: AppsScriptApiService,
) {
    val isEnabled: Boolean
        get() = config.isRemoteEnabled

    suspend fun fetchTests(): List<TestSummary> {
        val payload = executeApiCall {
            service.getTests(config.buildUrl(action = "tests"))
        }
        ensureOk(payload.status, payload.message)
        return payload.data.orEmpty().map { it.toDomain() }
    }

    suspend fun fetchQuestions(testId: String): List<SurveyQuestion> {
        val payload = executeApiCall {
            service.getQuestions(
                config.buildUrl(
                    action = "questions",
                    queryParams = mapOf("testId" to testId),
                ),
            )
        }
        ensureOk(payload.status, payload.message)
        return payload.data.orEmpty().map { it.toDomain() }
    }

    suspend fun submitSurvey(submission: SurveySubmission): SubmissionReceipt {
        return executeApiCall {
            service.submitSurvey(
                url = config.buildUrl(action = "submit"),
                body = submission.toRequestDto(),
            )
        }.toDomain()
    }

    private fun ensureOk(status: String, message: String?) {
        require(status == "ok") {
            message ?: "Apps Script returned an unexpected response"
        }
    }

    private inline fun <T> executeApiCall(block: () -> T): T {
        return try {
            block()
        } catch (error: Throwable) {
            throw mapApiError(error)
        }
    }

    private fun mapApiError(error: Throwable): Throwable {
        val message = error.message.orEmpty()
        val looksLikeHtmlLogin = message.contains("Use JsonReader.setStrictness", ignoreCase = true) ||
            message.contains("MalformedJsonException", ignoreCase = true) ||
            message.contains("<!doctype html>", ignoreCase = true) ||
            message.contains("<html", ignoreCase = true)

        if (looksLikeHtmlLogin) {
            return IllegalStateException(
                "El endpoint de Apps Script está devolviendo HTML en vez de JSON. " +
                    "Suele pasar cuando la Web App no está desplegada con acceso público o sigue pidiendo login de Google.",
            )
        }

        return error
    }
}
