package com.example.imagetopdf.utils

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun LightStatusBarIcon() {
    val view = LocalView.current
    if(!view.isInEditMode){
        DisposableEffect(Unit) {
            val window = (view.context as Activity).window
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = false

            onDispose {
                controller.isAppearanceLightStatusBars = true
            }
        }
    }
}