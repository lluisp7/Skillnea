package com.skillnea.mobile.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.skillnea.mobile.core.model.TestSummary
import com.skillnea.mobile.data.SurveyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DashboardUiState(
    val isLoading: Boolean = true,
    val tests: List<TestSummary> = emptyList(),
    val errorMessage: String? = null,
)

class DashboardViewModel(
    private val repository: SurveyRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, errorMessage = null)
            }
            runCatching { repository.getTests() }
                .onSuccess { tests ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            tests = tests,
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "No se pudieron cargar los tests.",
                        )
                    }
                }
        }
    }

    companion object {
        fun factory(repository: SurveyRepository) = viewModelFactory {
            initializer {
                DashboardViewModel(repository)
            }
        }
    }
}
