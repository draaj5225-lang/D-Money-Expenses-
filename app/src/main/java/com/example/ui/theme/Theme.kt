package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = Emerald500,
  onPrimary = Slate900,
  primaryContainer = Emerald800,
  onPrimaryContainer = Emerald100,
  secondary = Teal500,
  onSecondary = Slate900,
  secondaryContainer = Slate800,
  onSecondaryContainer = Teal100,
  tertiary = Amber400,
  onTertiary = Slate900,
  background = Color(0xFF0B1319),
  surface = Slate900,
  surfaceVariant = Slate800,
  onBackground = Color(0xFFF1F5F9),
  onSurface = Color(0xFFF1F5F9),
  onSurfaceVariant = Slate300,
  outline = Slate600
)

private val LightColorScheme = lightColorScheme(
  primary = Emerald700,
  onPrimary = Color.White,
  primaryContainer = Emerald50,
  onPrimaryContainer = Emerald900,
  secondary = Teal700,
  onSecondary = Color.White,
  secondaryContainer = Teal100,
  onSecondaryContainer = Emerald900,
  tertiary = Amber600,
  onTertiary = Color.White,
  background = Color(0xFFF8FAFC),
  surface = Color.White,
  surfaceVariant = Slate100,
  onBackground = Slate900,
  onSurface = Slate900,
  onSurfaceVariant = Slate600,
  outline = Slate300
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Use our handcrafted refined color scheme for consistent brand experience
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
    typography = Typography,
    content = content
  )
}
