package com.skillnea.mobile.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.skillnea.mobile.model.SubmissionReceipt
import com.skillnea.mobile.model.SurveyAnswer
import com.skillnea.mobile.model.SurveyQuestion
import com.skillnea.mobile.model.SurveySubmission
import com.skillnea.mobile.model.TestSummary

data class ApiEnvelopeDto<T>(
    @SerializedName("status")
    val status: String,
    @SerializedName("data")
    val data: T?,
    @SerializedName("message")
    val message: String? = null,
)

data class TestSummaryDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("estimatedMinutes")
    val estimatedMinutes: Int,
    @SerializedName("category")
    val category: String,
    @SerializedName("questionCount")
    val questionCount: Int,
    @SerializedName("active")
    val active: Boolean,
    @SerializedName("dispositionCount")
    val dispositionCount: Int? = null,
    @SerializedName("responseType")
    val responseType: String? = null,
)

data class SurveyQuestionDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("testId")
    val testId: String,
    @SerializedName("order")
    val order: Int,
    @SerializedName("prompt")
    val prompt: String,
    @SerializedName("helper")
    val helper: String,
    @SerializedName("dimension")
    val dimension: String,
    @SerializedName("disposition")
    val disposition: String? = null,
    @SerializedName("responseType")
    val responseType: String? = null,
    @SerializedName("rubricLevel")
    val rubricLevel: Int? = null,
    @SerializedName("rubric")
    val rubric: String? = null,
)

data class SurveySubmissionRequestDto(
    @SerializedName("participantEmail")
    val participantEmail: String,
    @SerializedName("participantName")
    val participantName: String?,
    @SerializedName("testId")
    val testId: String,
    @SerializedName("submittedAt")
    val submittedAt: String,
    @SerializedName("answers")
    val answers: List<SurveyAnswerRequestDto>,
)

data class SurveyAnswerRequestDto(
    @SerializedName("questionId")
    val questionId: String,
    @SerializedName("disposition")
    val disposition: String,
    @SerializedName("rubricLevel")
    val rubricLevel: Int,
    @SerializedName("answerText")
    val answerText: String,
    @SerializedName("score")
    val score: Int? = null,
)

data class SubmissionReceiptDto(
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("submissionId")
    val submissionId: String,
    @SerializedName("storedAnswers")
    val storedAnswers: Int? = null,
)

fun TestSummaryDto.toDomain(): TestSummary = TestSummary(
    id = id,
    title = normalizeRemoteText(title),
    description = normalizeRemoteText(description),
    estimatedMinutes = estimatedMinutes,
    category = normalizeRemoteText(category),
    questionCount = questionCount,
    active = active,
    dispositionCount = dispositionCount ?: 0,
    responseType = responseType ?: "text",
)

fun SurveyQuestionDto.toDomain(): SurveyQuestion = SurveyQuestion(
    id = id,
    testId = testId,
    order = order,
    prompt = normalizeRemoteText(prompt),
    helper = normalizeRemoteText(helper),
    dimension = normalizeRemoteText(dimension),
    disposition = normalizeRemoteText(disposition ?: dimension),
    responseType = responseType ?: "text",
    rubricLevel = rubricLevel ?: 0,
    rubric = normalizeRemoteText(rubric ?: ""),
)

fun SurveySubmission.toRequestDto(): SurveySubmissionRequestDto = SurveySubmissionRequestDto(
    participantEmail = participantEmail,
    participantName = participantName,
    testId = testId,
    submittedAt = submittedAt,
    answers = answers.map(SurveyAnswer::toRequestDto),
)

fun SurveyAnswer.toRequestDto(): SurveyAnswerRequestDto = SurveyAnswerRequestDto(
    questionId = questionId,
    disposition = disposition,
    rubricLevel = rubricLevel,
    answerText = answerText,
    score = score,
)

fun SubmissionReceiptDto.toDomain(): SubmissionReceipt = SubmissionReceipt(
    status = status,
    message = normalizeRemoteText(message),
    submissionId = submissionId,
    storedAnswers = storedAnswers ?: 0,
)

private fun normalizeRemoteText(value: String): String {
    val trimmed = value.trim()
    if (!trimmed.contains('Ã') && !trimmed.contains('Â')) {
        return trimmed
    }

    return runCatching {
        val normalized = trimmed
            .toByteArray(Charsets.ISO_8859_1)
            .toString(Charsets.UTF_8)
        if (normalized.contains('\uFFFD')) {
            trimmed
        } else {
            normalized
        }
    }.getOrDefault(trimmed)
}
