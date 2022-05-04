package com.example.myjetnews.utils

import android.app.Activity
import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.window.layout.WindowMetricsCalculator
import java.lang.IllegalArgumentException

enum class WindowSize { Compat, Medium, Expanded}

@Composable
fun Activity.rememberWindowSizeClass(): WindowSize {
    val windowSize = rememberWindowSize()

    val windowDpSize = with(LocalDensity.current) {
        windowSize.toDpSize()
    }
    return getWindowSizeClass(windowDpSize)
}

@Composable
private fun Activity.rememberWindowSize(): Size {
  val configuration = LocalConfiguration.current
  val windowMetrics = remember(){
      WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(this)
  }
    return windowMetrics.bounds.toComposeRect().size
}

@VisibleForTesting
fun getWindowSizeClass(windowDpSize: DpSize): WindowSize = when {
    windowDpSize.width < 0.dp -> throw IllegalArgumentException("Dp value cannot be negative")
    windowDpSize.width < 600.dp -> WindowSize.Compat
    windowDpSize.width < 840.dp -> WindowSize.Medium
    else -> WindowSize.Expanded
}