package com.skillnea.mobile.view.survey

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.skillnea.mobile.model.SurveyOutcome
import com.skillnea.mobile.model.SurveyUiState
import com.skillnea.mobile.view.theme.LineSoft
import com.skillnea.mobile.view.theme.PrimaryBlue
import com.skillnea.mobile.view.theme.SubtleText

@Composable
fun SurveyScreen(
    uiState: SurveyUiState,
    participantEmail: String,
    testTitle: String,
    onAnswerChanged: (String) -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onCompleted: (SurveyOutcome) -> Unit,
) {
    LaunchedEffect(uiState.completedOutcome) {
        uiState.completedOutcome?.let(onCompleted)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.errorMessage != null && uiState.questions.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = uiState.errorMessage,
                        style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.error),
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = onBack) {
                            Text("Volver")
                        }
                        OutlinedButton(onClick = onRetry) {
                            Text("Reintentar")
                        }
                    }
                }
            }

            else -> {
                val currentQuestion = uiState.currentQuestion
                if (currentQuestion == null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                    ) {
                        Text("No hay preguntas disponibles para este test.")
                    }
                } else {
                    val progress = if (uiState.questions.isEmpty()) 0f else {
                        (uiState.currentIndex + 1).toFloat() / uiState.questions.size.toFloat()
                    }

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.TopCenter,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 620.dp)
                                .padding(horizontal = 24.dp, vertical = 28.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(18.dp),
                        ) {
                            Text(
                                text = testTitle,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = PrimaryBlue,
                                    fontWeight = FontWeight.Bold,
                                ),
                            )

                            if (participantEmail.isNotBlank()) {
                                Text(
                                    text = participantEmail,
                                    style = MaterialTheme.typography.bodySmall.copy(color = SubtleText),
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = currentQuestion.disposition,
                                    style = MaterialTheme.typography.bodySmall.copy(color = SubtleText),
                                )
                                Text(
                                    text = "Pregunta ${uiState.currentIndex + 1} / ${uiState.questions.size}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = SubtleText),
                                )
                            }

                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.fillMaxWidth(),
                                color = PrimaryBlue,
                                trackColor = LineSoft,
                            )

                            Text(
                                text = currentQuestion.prompt,
                                style = MaterialTheme.typography.headlineSmall,
                            )

                            if (currentQuestion.rubric.isNotBlank()) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    HorizontalDivider(color = LineSoft)
                                    Text(
                                        text = "Nivel ${currentQuestion.rubricLevel}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = PrimaryBlue,
                                            fontWeight = FontWeight.Bold,
                                        ),
                                    )
                                    Text(
                                        text = currentQuestion.rubric,
                                        style = MaterialTheme.typography.bodySmall.copy(color = SubtleText),
                                    )
                                }
                            }

                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = uiState.answers[currentQuestion.id].orEmpty(),
                                onValueChange = onAnswerChanged,
                                label = { Text("Respuesta") },
                                placeholder = { Text("Escribe tu respuesta") },
                                minLines = 8,
                                maxLines = 12,
                            )

                            uiState.errorMessage?.let { message ->
                                Text(
                                    text = message,
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.error),
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                OutlinedButton(
                                    modifier = Modifier.weight(1f),
                                    onClick = onBack,
                                ) {
                                    Text("Salir")
                                }
                                OutlinedButton(
                                    modifier = Modifier.weight(1f),
                                    onClick = onPrevious,
                                    enabled = uiState.currentIndex > 0,
                                ) {
                                    Text("Anterior")
                                }
                                if (uiState.currentIndex == uiState.questions.lastIndex) {
                                    Button(
                                        modifier = Modifier.weight(1f),
                                        onClick = onSubmit,
                                        enabled = !uiState.isSubmitting,
                                    ) {
                                        Text(if (uiState.isSubmitting) "Enviando..." else "Enviar")
                                    }
                                } else {
                                    Button(
                                        modifier = Modifier.weight(1f),
                                        onClick = onNext,
                                    ) {
                                        Text("Siguiente")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
