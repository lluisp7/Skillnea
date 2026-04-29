package com.skillnea.mobile.model

data class SurveyUiState(
    val isLoading: Boolean = true,
    val questions: List<SurveyQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val answers: Map<String, String> = emptyMap(),
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val completedOutcome: SurveyOutcome? = null,
) {
    val currentQuestion: SurveyQuestion?
        get() = questions.getOrNull(currentIndex)
}
