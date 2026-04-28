package com.skillnea.mobile.feature.survey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.skillnea.mobile.core.model.SubmissionReceipt
import com.skillnea.mobile.core.model.SurveyOption
import com.skillnea.mobile.core.model.SurveyOutcome
import com.skillnea.mobile.core.model.SurveyQuestion
import com.skillnea.mobile.core.model.SurveySubmission
import com.skillnea.mobile.data.SurveyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant

data class SurveyUiState(
    val isLoading: Boolean = true,
    val questions: List<SurveyQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val answers: Map<String, SurveyOption> = emptyMap(),
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val completedOutcome: SurveyOutcome? = null,
) {
    val currentQuestion: SurveyQuestion?
        get() = questions.getOrNull(currentIndex)

    val answeredCount: Int
        get() = answers.size
}

class SurveyViewModel(
    private val repository: SurveyRepository,
    private val testId: String,
    private val testTitle: String,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SurveyUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadQuestions()
    }

    fun loadQuestions() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, errorMessage = null)
            }
            runCatching { repository.getQuestions(testId) }
                .onSuccess { questions ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            questions = questions.sortedBy(SurveyQuestion::order),
                            currentIndex = 0,
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "No se pudieron cargar las preguntas.",
                        )
                    }
                }
        }
    }

    fun selectOption(option: SurveyOption) {
        val question = uiState.value.currentQuestion ?: return
        val updatedAnswers = uiState.value.answers.toMutableMap()
        updatedAnswers[question.id] = option
        _uiState.update {
            it.copy(
                answers = updatedAnswers,
                errorMessage = null,
            )
        }
    }

    fun nextQuestion() {
        val currentIndex = uiState.value.currentIndex
        if (currentIndex < uiState.value.questions.lastIndex) {
            _uiState.update { it.copy(currentIndex = currentIndex + 1) }
        }
    }

    fun previousQuestion() {
        val currentIndex = uiState.value.currentIndex
        if (currentIndex > 0) {
            _uiState.update { it.copy(currentIndex = currentIndex - 1) }
        }
    }

    fun submit(participantEmail: String) {
        val state = uiState.value
        if (state.questions.isEmpty()) {
            _uiState.update {
                it.copy(errorMessage = "No hay preguntas cargadas para este test.")
            }
            return
        }
        if (state.answers.size != state.questions.size) {
            _uiState.update {
                it.copy(errorMessage = "Responde todas las preguntas antes de enviar.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(isSubmitting = true, errorMessage = null)
            }

            val submission = SurveySubmission(
                participantEmail = participantEmail,
                testId = testId,
                submittedAt = Instant.now().toString(),
                answers = state.questions.mapNotNull { question ->
                    val option = state.answers[question.id] ?: return@mapNotNull null
                    com.skillnea.mobile.core.model.SurveyAnswer(
                        questionId = question.id,
                        optionId = option.id,
                        score = option.value,
                    )
                },
            )

            runCatching { repository.submitSurvey(submission) }
                .onSuccess { receipt ->
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            completedOutcome = createOutcome(
                                receipt = receipt,
                                questions = state.questions,
                                answers = state.answers,
                            ),
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = "No se pudieron enviar las respuestas.",
                        )
                    }
                }
        }
    }

    fun clearCompletedOutcome() {
        _uiState.update {
            it.copy(completedOutcome = null)
        }
    }

    private fun createOutcome(
        receipt: SubmissionReceipt,
        questions: List<SurveyQuestion>,
        answers: Map<String, SurveyOption>,
    ): SurveyOutcome {
        val average = answers.values.map(SurveyOption::value).average()
        val strongestDimension = questions
            .groupBy { it.dimension }
            .maxByOrNull { (_, groupedQuestions) ->
                groupedQuestions.mapNotNull { question ->
                    answers[question.id]?.value?.toDouble()
                }.average()
            }
            ?.key
            .orEmpty()
            .ifBlank { "General balance" }

        val message = when {
            average >= 3.5 -> "Perfil muy consistente y estable para este set de señales."
            average >= 2.5 -> "Resultado equilibrado con margen claro para profundizar en detalles."
            else -> "Se detectan oportunidades de desarrollo que conviene revisar con más contexto."
        }

        return SurveyOutcome(
            testTitle = testTitle,
            averageScore = average,
            strongestDimension = strongestDimension,
            message = message,
            submissionId = receipt.submissionId,
            storageMode = receipt.status,
        )
    }

    companion object {
        fun factory(
            repository: SurveyRepository,
            testId: String,
            testTitle: String,
        ) = viewModelFactory {
            initializer {
                SurveyViewModel(
                    repository = repository,
                    testId = testId,
                    testTitle = testTitle,
                )
            }
        }
    }
}
