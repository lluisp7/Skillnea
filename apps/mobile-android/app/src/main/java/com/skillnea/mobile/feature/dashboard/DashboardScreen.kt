package com.skillnea.mobile.feature.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.skillnea.mobile.core.model.TestSummary
import com.skillnea.mobile.ui.theme.PrimaryBlue
import com.skillnea.mobile.ui.theme.SubtleText

@Composable
fun DashboardScreen(
    uiState: DashboardUiState,
    participantEmail: String,
    isRemoteConfigured: Boolean,
    onRefresh: () -> Unit,
    onStartTest: (TestSummary) -> Unit,
    onBackToAccess: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Available Assessments",
                    style = MaterialTheme.typography.headlineMedium,
                )
                Text(
                    text = participantEmail,
                    style = MaterialTheme.typography.bodyLarge.copy(color = PrimaryBlue),
                )
                Text(
                    text = if (isRemoteConfigured) {
                        "La app leerá preguntas desde Apps Script."
                    } else {
                        "Modo demo activo: se están usando tests locales de muestra."
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(color = SubtleText),
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onBackToAccess) {
                    Text("Cambiar acceso")
                }
                OutlinedButton(onClick = onRefresh) {
                    Text("Recargar")
                }
            }

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.errorMessage != null -> {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = uiState.errorMessage,
                            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.error),
                        )
                        Button(onClick = onRefresh) {
                            Text("Intentar de nuevo")
                        }
                    }
                }

                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(uiState.tests, key = { it.id }) { test ->
                            TestCard(test = test, onStartTest = { onStartTest(test) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TestCard(
    test: TestSummary,
    onStartTest: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = test.category.uppercase(),
                style = MaterialTheme.typography.labelMedium.copy(
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Bold,
                ),
            )
            Text(
                text = test.title,
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                text = test.description,
                style = MaterialTheme.typography.bodyMedium.copy(color = SubtleText),
            )
            Text(
                text = "${test.questionCount} preguntas · ${test.estimatedMinutes} min",
                style = MaterialTheme.typography.bodySmall.copy(color = SubtleText),
            )
            Button(onClick = onStartTest) {
                Text("Comenzar test")
            }
        }
    }
}
