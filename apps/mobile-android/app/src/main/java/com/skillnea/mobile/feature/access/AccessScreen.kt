package com.skillnea.mobile.feature.access

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.KeyboardType
import com.skillnea.mobile.ui.components.PrimaryActionButton
import com.skillnea.mobile.ui.components.SoftInfoCard
import com.skillnea.mobile.ui.theme.AccentMist
import com.skillnea.mobile.ui.theme.LineSoft
import com.skillnea.mobile.ui.theme.PrimaryBlue
import com.skillnea.mobile.ui.theme.SubtleText

@Composable
fun AccessScreen(
    uiState: AccessUiState,
    isRemoteConfigured: Boolean,
    onEmailChanged: (String) -> Unit,
    onJoinStudy: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        AccentMist.copy(alpha = 0.55f),
                        MaterialTheme.colorScheme.background,
                    ),
                ),
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp),
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    verticalArrangement = Arrangement.spacedBy(30.dp),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "TEST",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = PrimaryBlue,
                                letterSpacing = 3.sp,
                            ),
                        )
                        Text(
                            text = buildAnnotatedString {
                                append("Discover the\n")
                                withStyle(SpanStyle(color = PrimaryBlue)) {
                                    append("unseen")
                                }
                                append("\npatterns.")
                            },
                            style = MaterialTheme.typography.displayMedium,
                        )
                        Text(
                            text = "A refined inquiry into the architecture of your personality.",
                            style = MaterialTheme.typography.bodyLarge.copy(color = SubtleText),
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "EMAIL IDENTIFIER",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SubtleText.copy(alpha = 0.72f),
                                letterSpacing = 1.4.sp,
                            ),
                        )
                        BasicTextField(
                            value = uiState.email,
                            onValueChange = onEmailChanged,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            textStyle = MaterialTheme.typography.headlineSmall.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Light,
                            ),
                            decorationBox = { innerTextField ->
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    if (uiState.email.isBlank()) {
                                        Text(
                                            text = "jane@journal.edu",
                                            style = MaterialTheme.typography.headlineSmall.copy(
                                                color = SubtleText.copy(alpha = 0.28f),
                                                fontWeight = FontWeight.Light,
                                            ),
                                        )
                                    }
                                    innerTextField()
                                    HorizontalDivider(color = LineSoft)
                                }
                            },
                        )
                        uiState.errorMessage?.let { message ->
                            Text(
                                text = message,
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.error),
                            )
                        }
                    }

                    PrimaryActionButton(
                        text = "Join Study",
                        icon = Icons.AutoMirrored.Filled.ArrowForward,
                        onClick = onJoinStudy,
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        HorizontalDivider(color = LineSoft)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(PrimaryBlue, CircleShape),
                            )
                            Text(
                                text = if (isRemoteConfigured) "Apps Script session ready" else "Demo session active",
                                style = MaterialTheme.typography.bodySmall.copy(color = SubtleText),
                            )
                        }
                        Text(
                            text = "Privacy Protocol",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SubtleText.copy(alpha = 0.7f),
                                textDecoration = TextDecoration.Underline,
                            ),
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                SoftInfoCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.Schedule,
                    title = "12 Minutes",
                    subtitle = "Estimated completion",
                )
                SoftInfoCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.Lock,
                    title = if (isRemoteConfigured) "Connected" else "Anonymous",
                    subtitle = if (isRemoteConfigured) "Apps Script session" else "Academic data policy",
                )
            }
        }
    }
}
