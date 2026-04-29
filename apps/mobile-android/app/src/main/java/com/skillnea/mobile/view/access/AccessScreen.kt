package com.skillnea.mobile.view.access

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.skillnea.mobile.model.AccessUiState
import com.skillnea.mobile.view.theme.PrimaryBlue
import com.skillnea.mobile.view.theme.SubtleText

@Composable
fun AccessScreen(
    uiState: AccessUiState,
    onEmailChanged: (String) -> Unit,
    onJoinStudy: () -> Unit,
    onRetry: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 520.dp)
                    .padding(horizontal = 24.dp, vertical = 32.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                Text(
                    text = "skillnea",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = PrimaryBlue,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )

                Text(
                    text = uiState.assessment?.title ?: "Acceso a la encuesta",
                    style = MaterialTheme.typography.displaySmall,
                )

                when {
                    uiState.isLoading -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                            )
                            Text(
                                text = "Cargando encuesta",
                                style = MaterialTheme.typography.bodyMedium.copy(color = SubtleText),
                            )
                        }
                    }

                    uiState.assessment != null -> {
                        Text(
                            text = "${uiState.assessment.questionCount} preguntas · ${uiState.assessment.estimatedMinutes} min",
                            style = MaterialTheme.typography.bodyMedium.copy(color = SubtleText),
                        )
                    }
                }

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.email,
                    onValueChange = onEmailChanged,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    label = { Text("Email") },
                    placeholder = { Text("tu@email.com") },
                    supportingText = {
                        Text("Lo usaremos para asociar tu respuesta.")
                    },
                )

                uiState.errorMessage?.let { message ->
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.error),
                    )
                }

                Button(
                    onClick = onJoinStudy,
                    enabled = !uiState.isLoading && uiState.assessment != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                ) {
                    Text("Empezar")
                }

                if (!uiState.isLoading && uiState.assessment == null) {
                    OutlinedButton(
                        onClick = onRetry,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Reintentar")
                    }
                }

                uiState.assessment?.description
                    ?.takeIf(String::isNotBlank)
                    ?.let { description ->
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodySmall.copy(color = SubtleText),
                        )
                    }
            }
        }
    }
}
