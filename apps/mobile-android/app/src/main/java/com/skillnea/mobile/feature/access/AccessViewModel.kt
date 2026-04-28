package com.skillnea.mobile.feature.access

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AccessUiState(
    val email: String = "",
    val errorMessage: String? = null,
)

class AccessViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AccessUiState())
    val uiState = _uiState.asStateFlow()

    fun onEmailChanged(value: String) {
        _uiState.update {
            it.copy(
                email = value,
                errorMessage = null,
            )
        }
    }

    fun validateAndGetEmail(): String? {
        val trimmed = uiState.value.email.trim()
        if (!Patterns.EMAIL_ADDRESS.matcher(trimmed).matches()) {
            _uiState.update {
                it.copy(errorMessage = "Introduce un email válido para iniciar el test.")
            }
            return null
        }
        return trimmed
    }

    companion object {
        fun factory() = viewModelFactory {
            initializer {
                AccessViewModel()
            }
        }
    }
}
