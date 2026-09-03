package com.lectostart.app.rescue.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lectostart.app.core.ui.PlaceholderScreen

/** Placeholder de Fase 0 (T-006). UI real: US-13 en docs/USER_STORIES.md. */
@Composable
fun RescueModeScreen(onStartFiveMin: () -> Unit, modifier: Modifier = Modifier) {
  PlaceholderScreen(routeName = "🚨 Modo Rescate", onNext = onStartFiveMin, nextLabel = "Comenzar 5 minutos", modifier = modifier)
}
