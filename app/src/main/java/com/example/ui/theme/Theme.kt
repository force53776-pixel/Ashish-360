package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = LuminousGold,
    onPrimary = RoyalIndigo,
    primaryContainer = ElevatedPlum,
    onPrimaryContainer = CreamText,
    secondary = SoftCoral,
    onSecondary = ObsidianNight,
    secondaryContainer = Color(0xFF3A2431),
    onSecondaryContainer = Color(0xFFFFDAD4),
    tertiary = Color(0xFF6EE7B7),
    onTertiary = Color(0xFF033925),
    background = ObsidianNight,
    onBackground = CreamText,
    surface = MidnightSurface,
    onSurface = CreamText,
    surfaceVariant = ElevatedPlum,
    onSurfaceVariant = LavenderMuted,
    outline = DarkBorder,
    outlineVariant = Color(0xFF282442),
    error = Color(0xFFFFB4AB)
)

private val LightColorScheme = lightColorScheme(
    primary = RoyalIndigo,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE9E5FF),
    onPrimaryContainer = RoyalIndigo,
    secondary = MarigoldGold,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = Color(0xFF78350F),
    tertiary = TerracottaRose,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFEE2E2),
    onTertiaryContainer = Color(0xFF7F1D1D),
    background = IvoryParchment,
    onBackground = InkDark,
    surface = SoftCreamCard,
    onSurface = InkDark,
    surfaceVariant = WarmSand,
    onSurfaceVariant = MutedSlate,
    outline = WarmBorder,
    outlineVariant = Color(0xFFEDE5D6),
    error = Color(0xFFBA1A1A)
)

val AppShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun MyLifeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
