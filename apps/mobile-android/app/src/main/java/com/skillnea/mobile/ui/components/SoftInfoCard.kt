package com.skillnea.mobile.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.skillnea.mobile.ui.theme.PrimaryBlue
import com.skillnea.mobile.ui.theme.SubtleText

@Composable
fun SoftInfoCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    subtitle: String,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryBlue.copy(alpha = 0.08f)),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                modifier = Modifier.size(18.dp),
                imageVector = icon,
                tint = PrimaryBlue,
                contentDescription = null,
            )
            Text(text = title, style = MaterialTheme.typography.titleSmall)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(color = SubtleText),
            )
        }
    }
}
