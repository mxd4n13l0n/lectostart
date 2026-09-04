package com.lectostart.app.comprehension.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lectostart.app.core.ui.PlaceholderScreen

/** Placeholder de Fase 0 (T-006) pendiente. UI real: US-10 en docs/USER_STORIES.md. Questions (US-09) ya es real: ver QuestionsScreen.kt. */
@Composable
fun SessionResultScreen(onNext: () -> Unit, modifier: Modifier = Modifier) {
  PlaceholderScreen(routeName = "Sesión completada", onNext = onNext, nextLabel = "Continuar mañana", modifier = modifier)
}
