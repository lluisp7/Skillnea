package com.skillnea.mobile.model

data class TestSummary(
    val id: String,
    val title: String,
    val description: String,
    val estimatedMinutes: Int,
    val category: String,
    val questionCount: Int,
    val active: Boolean,
    val dispositionCount: Int = 0,
    val responseType: String = "text",
)

data class SurveyQuestion(
    val id: String,
    val testId: String,
    val order: Int,
    val prompt: String,
    val helper: String,
    val dimension: String,
    val disposition: String,
    val responseType: String,
    val rubricLevel: Int,
    val rubric: String,
)

data class SurveyAnswer(
    val questionId: String,
    val disposition: String,
    val rubricLevel: Int,
    val answerText: String,
    val score: Int? = null,
)

data class SurveySubmission(
    val participantEmail: String,
    val participantName: String? = null,
    val testId: String,
    val submittedAt: String,
    val answers: List<SurveyAnswer>,
)

data class SubmissionReceipt(
    val status: String,
    val message: String,
    val submissionId: String,
    val storedAnswers: Int = 0,
)

data class SurveyOutcome(
    val testTitle: String,
    val answeredQuestions: Int,
    val message: String,
    val submissionId: String,
    val storageMode: String,
)
