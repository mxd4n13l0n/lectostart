package com.lectostart.app.procrastination.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lectostart.app.core.ui.PlaceholderScreen

/** Placeholders de Fase 0 (T-006). UI real: US-04 en docs/USER_STORIES.md. */
@Composable
fun DiagnosticScreen(onNext: () -> Unit, modifier: Modifier = Modifier) {
  PlaceholderScreen(routeName = "Diagnóstico de procrastinación", onNext = onNext, nextLabel = "Finalizar diagnóstico", modifier = modifier)
}

@Composable
fun DiagnosticResultScreen(onNext: () -> Unit, modifier: Modifier = Modifier) {
  PlaceholderScreen(routeName = "Tu punto de partida", onNext = onNext, modifier = modifier)
}
