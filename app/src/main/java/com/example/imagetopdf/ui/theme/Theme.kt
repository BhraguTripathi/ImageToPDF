package com.example.imagetopdf.ui.theme

import android.app.Activity
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

private val LightColorScheme = lightColorScheme(
    primary = BrandPurple,        // Your main purple
    secondary = BrandBlueLight,   // Your secondary blue
    tertiary = AccentRed,         // Your accent/error color

    background = AppBackGround,   // Your light gray background
    surface = CardSurface,        // Your white cards
    onPrimary = Color.White,      // Text color on top of Primary (Purple) buttons
    onSecondary = Color.White,    // Text color on top of Secondary buttons
    onBackground = TextPrimary,   // Text color on top of Background
    onSurface = TextPrimary       // Text color on top of Surface
)

private val DarkColorScheme = darkColorScheme(
    // For now, you can just map these to darker versions or keep them similar
    primary = BrandPurple,
    secondary = BrandBlueLight,
    tertiary = AccentRed,

    background = Color(0xFF121212), // Standard dark background
    surface = Color(0xFF1E1E1E),    // Slightly lighter dark for cards
    onPrimary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun ImageToPDFTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
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