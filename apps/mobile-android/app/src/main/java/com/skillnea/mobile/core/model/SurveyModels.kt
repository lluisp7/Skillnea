package com.skillnea.mobile.core.model

data class TestSummary(
    val id: String,
    val title: String,
    val description: String,
    val estimatedMinutes: Int,
    val category: String,
    val questionCount: Int,
    val active: Boolean,
)

data class SurveyOption(
    val id: String,
    val label: String,
    val value: Int,
)

data class SurveyQuestion(
    val id: String,
    val testId: String,
    val order: Int,
    val prompt: String,
    val helper: String,
    val dimension: String,
    val options: List<SurveyOption>,
)

data class SurveyAnswer(
    val questionId: String,
    val optionId: String,
    val score: Int,
)

data class SurveySubmission(
    val participantEmail: String,
    val testId: String,
    val submittedAt: String,
    val answers: List<SurveyAnswer>,
)

data class SubmissionReceipt(
    val status: String,
    val message: String,
    val submissionId: String,
)

data class SurveyOutcome(
    val testTitle: String,
    val averageScore: Double,
    val strongestDimension: String,
    val message: String,
    val submissionId: String,
    val storageMode: String,
)
