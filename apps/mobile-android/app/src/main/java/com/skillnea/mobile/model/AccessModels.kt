package com.skillnea.mobile.model

data class AccessSession(
    val email: String,
    val testId: String,
    val testTitle: String,
)

data class AccessUiState(
    val email: String = "",
    val isLoading: Boolean = true,
    val assessment: TestSummary? = null,
    val errorMessage: String? = null,
    val sessionReady: AccessSession? = null,
)
