package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = Color(0xFFD1E4FF),
    secondary = Color(0xFFA8C7FF),
    tertiary = Color(0xFF004A77),
    background = Color(0xFF1A1C1E),
    surface = Color(0xFF1A1C1E),
    onPrimary = Color(0xFF00315C),
    onSecondary = Color(0xFF1A1C1E),
    onTertiary = Color(0xFFD1E4FF),
    onBackground = Color(0xFFE2E2E6),
    onSurface = Color(0xFFE2E2E6),
  )

private val LightColorScheme = DarkColorScheme // Enforcement of dark theme

@Composable
fun MyApplicationTheme(
  // Enforce dark theme
  darkTheme: Boolean = true,
  // Disable dynamic color
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
