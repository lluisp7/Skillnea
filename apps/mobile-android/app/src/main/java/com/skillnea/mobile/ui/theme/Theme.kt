package com.skillnea.mobile.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val SkillneaColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = CardWhite,
    secondary = SecondaryBlue,
    tertiary = TertiaryBlue,
    background = NeutralBackground,
    surface = CardWhite,
    onSurface = Ink,
    onBackground = Ink,
)

@Composable
fun SkillneaTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = SkillneaColorScheme,
        typography = SkillneaTypography,
        content = content,
    )
}
