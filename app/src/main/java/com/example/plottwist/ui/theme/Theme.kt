package com.example.plottwist.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val TaintedCupColorScheme = darkColorScheme(
    primary = RichGold,
    onPrimary = DeepNavy,
    primaryContainer = DarkGold,
    onPrimaryContainer = CreamWhite,
    secondary = BrightGold,
    onSecondary = DeepNavy,
    secondaryContainer = MidNavy,
    onSecondaryContainer = CreamWhite,
    background = DeepNavy,
    onBackground = CreamWhite,
    surface = MidNavy,
    onSurface = CreamWhite,
    surfaceVariant = MidNavy,
    onSurfaceVariant = DarkCream,
    outline = RichGold,
    outlineVariant = DarkGold
)

@Composable
fun PlottwistTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = TaintedCupColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = DeepNavy.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
