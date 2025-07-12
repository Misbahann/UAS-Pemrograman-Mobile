package com.example.appcontact.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = SkyBlue_Dark,
    onPrimary = OnSkyBlue_Dark,
    primaryContainer = SkyBlueContainer_Dark,
    onPrimaryContainer = OnSkyBlueContainer_Dark,
    secondary = Aqua_Dark,
    onSecondary = OnAqua_Dark,
    secondaryContainer = AquaContainer_Dark,
    onSecondaryContainer = OnAquaContainer_Dark,
    tertiary = Peach_Dark,
    onTertiary = OnPeach_Dark,
    tertiaryContainer = PeachContainer_Dark,
    onTertiaryContainer = OnPeachContainer_Dark,
    background = DarkBackground,
    onBackground = Color(0xFFE3E2E6),
    surface = DarkBackground,
    onSurface = Color(0xFFE3E2E6)
)

private val LightColorScheme = lightColorScheme(
    primary = Navy_Light,
    onPrimary = OnNavy_Light,
    primaryContainer = NavyContainer_Light,
    onPrimaryContainer = OnNavyContainer_Light,
    secondary = Teal_Light,
    onSecondary = OnTeal_Light,
    secondaryContainer = TealContainer_Light,
    onSecondaryContainer = OnTealContainer_Light,
    tertiary = Orange_Light,
    onTertiary = OnOrange_Light,
    tertiaryContainer = OrangeContainer_Light,
    onTertiaryContainer = OnOrangeContainer_Light,
    background = LightBackground,
    onBackground = Color(0xFF1A1C1E),
    surface = LightBackground,
    onSurface = Color(0xFF1A1C1E)
)

@Composable
fun AppContactTheme(
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