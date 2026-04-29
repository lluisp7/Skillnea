package com.skillnea.mobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.skillnea.mobile.data.repository.SurveyRepository
import com.skillnea.mobile.model.SubmissionReceipt
import com.skillnea.mobile.model.SurveyAnswer
import com.skillnea.mobile.model.SurveyOutcome
import com.skillnea.mobile.model.SurveyQuestion
import com.skillnea.mobile.model.SurveySubmission
import com.skillnea.mobile.model.SurveyUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant

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
                    if (questions.isEmpty()) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "La API no ha devuelto preguntas para esta encuesta.",
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                questions = questions.sortedBy(SurveyQuestion::order),
                                currentIndex = 0,
                            )
                        }
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "No se pudieron cargar las preguntas.",
                        )
                    }
                }
        }
    }

    fun updateAnswer(value: String) {
        val question = uiState.value.currentQuestion ?: return
        val updatedAnswers = uiState.value.answers.toMutableMap()
        updatedAnswers[question.id] = value
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
        if (state.questions.any { state.answers[it.id].isNullOrBlank() }) {
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
                answers = state.questions.map { question ->
                    SurveyAnswer(
                        questionId = question.id,
                        disposition = question.disposition,
                        rubricLevel = question.rubricLevel,
                        answerText = state.answers[question.id].orEmpty().trim(),
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
                                answeredQuestions = state.questions.size,
                            ),
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = error.message ?: "No se pudieron enviar las respuestas.",
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
        answeredQuestions: Int,
    ): SurveyOutcome {
        return SurveyOutcome(
            testTitle = testTitle,
            answeredQuestions = answeredQuestions,
            message = "Tus respuestas han quedado registradas. La valoración posterior podrá hacerse desde la rúbrica y el panel de administración.",
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
