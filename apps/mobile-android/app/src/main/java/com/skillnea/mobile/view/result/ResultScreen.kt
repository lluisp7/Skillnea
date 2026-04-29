package com.skillnea.mobile.view.result

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.skillnea.mobile.model.SurveyOutcome
import com.skillnea.mobile.view.theme.PrimaryBlue
import com.skillnea.mobile.view.theme.SubtleText

@Composable
fun ResultScreen(
    outcome: SurveyOutcome,
    onBackToStart: () -> Unit,
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
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = "Encuesta enviada",
                    style = MaterialTheme.typography.headlineMedium,
                )
                Text(
                    text = outcome.testTitle,
                    style = MaterialTheme.typography.bodyLarge.copy(color = PrimaryBlue),
                )
                Text(
                    text = "${outcome.answeredQuestions} respuestas registradas",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = outcome.message,
                    style = MaterialTheme.typography.bodySmall.copy(color = SubtleText),
                )
                Text(
                    text = "Referencia ${outcome.submissionId}",
                    style = MaterialTheme.typography.bodySmall.copy(color = SubtleText),
                )
                Button(
                    onClick = onBackToStart,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Volver al inicio")
                }
            }
        }
    }
}
