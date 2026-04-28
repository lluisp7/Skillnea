package com.skillnea.mobile.feature.survey

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.skillnea.mobile.core.model.SurveyOption
import com.skillnea.mobile.core.model.SurveyOutcome
import com.skillnea.mobile.ui.theme.PrimaryBlue
import com.skillnea.mobile.ui.theme.SubtleText

@Composable
fun SurveyScreen(
    uiState: SurveyUiState,
    participantEmail: String,
    testTitle: String,
    onSelectOption: (SurveyOption) -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
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
                    OutlinedButton(onClick = onBack) {
                        Text("Volver")
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

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp, vertical = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp),
                    ) {
                        item {
                            Text(
                                text = testTitle,
                                style = MaterialTheme.typography.headlineMedium,
                            )
                            Text(
                                text = participantEmail,
                                style = MaterialTheme.typography.bodyMedium.copy(color = PrimaryBlue),
                            )
                        }

                        item {
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }

                        item {
                            Text(
                                text = "Pregunta ${uiState.currentIndex + 1} de ${uiState.questions.size}",
                                style = MaterialTheme.typography.bodySmall.copy(color = SubtleText),
                            )
                        }

                        item {
                            Card(
                                shape = RoundedCornerShape(28.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    Text(
                                        text = currentQuestion.dimension.uppercase(),
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            color = PrimaryBlue,
                                            fontWeight = FontWeight.Bold,
                                        ),
                                    )
                                    Text(
                                        text = currentQuestion.prompt,
                                        style = MaterialTheme.typography.headlineSmall,
                                    )
                                    Text(
                                        text = currentQuestion.helper,
                                        style = MaterialTheme.typography.bodyMedium.copy(color = SubtleText),
                                    )
                                }
                            }
                        }

                        items(currentQuestion.options.size) { index ->
                            val option = currentQuestion.options[index]
                            val selected = uiState.answers[currentQuestion.id]?.id == option.id
                            OptionCard(
                                option = option,
                                selected = selected,
                                onClick = { onSelectOption(option) },
                            )
                        }

                        item {
                            uiState.errorMessage?.let { message ->
                                Text(
                                    text = message,
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.error),
                                )
                            }
                        }

                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedButton(onClick = onBack) {
                                    Text("Salir")
                                }
                                OutlinedButton(
                                    onClick = onPrevious,
                                    enabled = uiState.currentIndex > 0,
                                ) {
                                    Text("Anterior")
                                }
                                if (uiState.currentIndex == uiState.questions.lastIndex) {
                                    Button(
                                        onClick = onSubmit,
                                        enabled = !uiState.isSubmitting,
                                    ) {
                                        Text(if (uiState.isSubmitting) "Enviando..." else "Enviar")
                                    }
                                } else {
                                    Button(onClick = onNext) {
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

@Composable
private fun OptionCard(
    option: SurveyOption,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val containerColor = if (selected) {
        PrimaryBlue.copy(alpha = 0.12f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 8.dp else 3.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .background(
                        color = if (selected) PrimaryBlue else PrimaryBlue.copy(alpha = 0.14f),
                        shape = CircleShape,
                    ),
            )
            Column {
                Text(
                    text = option.label,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = "Valor ${option.value}",
                    style = MaterialTheme.typography.bodySmall.copy(color = SubtleText),
                )
            }
        }
    }
}
