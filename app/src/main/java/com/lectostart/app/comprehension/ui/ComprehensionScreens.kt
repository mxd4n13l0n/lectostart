package com.lectostart.app.comprehension.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lectostart.app.core.ui.PlaceholderScreen

/** Placeholders de Fase 0 (T-006). UI real: US-09/US-10 en docs/USER_STORIES.md. */
@Composable
fun QuestionsScreen(onNext: () -> Unit, modifier: Modifier = Modifier) {
  PlaceholderScreen(routeName = "¿Cuánto comprendiste?", onNext = onNext, nextLabel = "Enviar respuestas", modifier = modifier)
}

@Composable
fun SessionResultScreen(onNext: () -> Unit, modifier: Modifier = Modifier) {
  PlaceholderScreen(routeName = "Sesión completada", onNext = onNext, nextLabel = "Continuar mañana", modifier = modifier)
}
