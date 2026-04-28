package com.skillnea.mobile.feature.result

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.skillnea.mobile.core.model.SurveyOutcome
import com.skillnea.mobile.ui.theme.PrimaryBlue
import com.skillnea.mobile.ui.theme.SubtleText
import java.util.Locale

@Composable
fun ResultScreen(
    outcome: SurveyOutcome,
    onBackToCatalog: () -> Unit,
    onExitSession: () -> Unit,
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
            Text(
                text = "Submission completed",
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = outcome.testTitle,
                style = MaterialTheme.typography.bodyLarge.copy(color = PrimaryBlue),
            )

            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = String.format(Locale.US, "%.2f / 4.00", outcome.averageScore),
                        style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                    )
                    Text(
                        text = "Fortaleza principal: ${outcome.strongestDimension}",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = outcome.message,
                        style = MaterialTheme.typography.bodyMedium.copy(color = SubtleText),
                    )
                    Text(
                        text = "Storage: ${outcome.storageMode} · Ref: ${outcome.submissionId}",
                        style = MaterialTheme.typography.bodySmall.copy(color = SubtleText),
                    )
                }
            }

            Button(onClick = onBackToCatalog) {
                Text("Volver al catálogo")
            }
            OutlinedButton(onClick = onExitSession) {
                Text("Cerrar sesión")
            }
        }
    }
}
