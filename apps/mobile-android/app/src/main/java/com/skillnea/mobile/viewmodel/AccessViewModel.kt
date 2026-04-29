package com.skillnea.mobile.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.skillnea.mobile.data.repository.SurveyRepository
import com.skillnea.mobile.model.AccessSession
import com.skillnea.mobile.model.AccessUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AccessViewModel(
    private val repository: SurveyRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AccessUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadAssessment()
    }

    fun onEmailChanged(value: String) {
        _uiState.update {
            it.copy(
                email = value,
                errorMessage = null,
            )
        }
    }

    fun loadAssessment() {
        if (!repository.isRemoteConfigured) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "La API remota no está configurada en este build.",
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                )
            }

            runCatching {
                repository.getTests().firstOrNull()
                    ?: error("No hay ningún test activo en el Excel.")
            }
                .onSuccess { test ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            assessment = test,
                            errorMessage = null,
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            assessment = null,
                            errorMessage = error.message ?: "No se pudo cargar la encuesta desde la API.",
                        )
                    }
                }
        }
    }

    fun startSurvey() {
        val state = uiState.value
        val trimmed = state.email.trim()
        if (!Patterns.EMAIL_ADDRESS.matcher(trimmed).matches()) {
            _uiState.update {
                it.copy(errorMessage = "Introduce un email válido para iniciar el test.")
            }
            return
        }

        val assessment = state.assessment
        if (assessment == null) {
            _uiState.update {
                it.copy(errorMessage = "La encuesta todavía no está disponible.")
            }
            return
        }

        _uiState.update {
            it.copy(
                sessionReady = AccessSession(
                    email = trimmed,
                    testId = assessment.id,
                    testTitle = assessment.title,
                ),
                errorMessage = null,
            )
        }
    }

    fun consumeSession() {
        _uiState.update {
            it.copy(sessionReady = null)
        }
    }

    companion object {
        fun factory(repository: SurveyRepository) = viewModelFactory {
            initializer {
                AccessViewModel(repository)
            }
        }
    }
}
