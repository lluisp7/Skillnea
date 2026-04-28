package com.skillnea.mobile.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.skillnea.mobile.ui.theme.PrimaryBlue

@Composable
fun PrimaryActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
        shape = MaterialTheme.shapes.medium,
    ) {
        Text(text = text)
        Icon(imageVector = icon, contentDescription = null)
    }
}
