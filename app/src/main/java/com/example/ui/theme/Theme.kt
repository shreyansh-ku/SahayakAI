package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ClinicalLightColorScheme = lightColorScheme(
    primary = ClinicalPrimary,
    onPrimary = ClinicalOnPrimary,
    primaryContainer = ClinicalPrimaryContainer,
    onPrimaryContainer = ClinicalOnPrimaryContainer,
    secondary = ClinicalSecondary,
    onSecondary = ClinicalOnPrimary,
    secondaryContainer = ClinicalSecondaryContainer,
    onSecondaryContainer = ClinicalOnSecondaryContainer,
    tertiary = ClinicalTertiary,
    onTertiary = ClinicalOnPrimary,
    tertiaryContainer = ClinicalTertiaryContainer,
    onTertiaryContainer = ClinicalOnTertiaryFixedVariant,
    error = ClinicalError,
    onError = ClinicalOnPrimary,
    errorContainer = ClinicalErrorContainer,
    onErrorContainer = ClinicalOnErrorContainer,
    background = ClinicalSurface,
    onBackground = ClinicalOnSurface,
    surface = ClinicalSurface,
    onSurface = ClinicalOnSurface,
    surfaceVariant = ClinicalSurfaceContainerHighest,
    onSurfaceVariant = ClinicalOnSurfaceVariant,
    surfaceContainerLowest = ClinicalSurfaceContainerLowest,
    surfaceContainerLow = ClinicalSurfaceContainerLow,
    surfaceContainer = ClinicalSurfaceContainer,
    surfaceContainerHigh = ClinicalSurfaceContainerHigh,
    surfaceContainerHighest = ClinicalSurfaceContainerHighest,
    outline = ClinicalOutline,
    outlineVariant = ClinicalOutlineVariant,
    inverseSurface = ClinicalInverseSurface,
    inverseOnSurface = ClinicalInverseOnSurface,
    inversePrimary = ClinicalPrimaryFixedDim
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false, // Force our high-contrast clinical theme for consistency
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ClinicalLightColorScheme,
        typography = Typography,
        content = content
    )
}
