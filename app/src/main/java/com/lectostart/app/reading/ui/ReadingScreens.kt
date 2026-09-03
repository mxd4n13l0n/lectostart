package com.lectostart.app.reading.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lectostart.app.core.ui.PlaceholderScreen

/** Placeholders de Fase 0 (T-006). UI real: US-05 a US-08 en docs/USER_STORIES.md. */
@Composable
fun ReadingListScreen(onNext: () -> Unit, modifier: Modifier = Modifier) {
  PlaceholderScreen(routeName = "Elegir lectura", onNext = onNext, modifier = modifier)
}

@Composable
fun DurationPickerScreen(onNext: () -> Unit, onRescueMode: () -> Unit, modifier: Modifier = Modifier) {
  PlaceholderScreen(
    routeName = "¿Cuánto tiempo tienes?",
    onNext = onNext,
    onSecondary = onRescueMode,
    secondaryLabel = "Modo Rescate",
    modifier = modifier,
  )
}

@Composable
fun StartFiveMinScreen(onNext: () -> Unit, onRescueMode: () -> Unit, modifier: Modifier = Modifier) {
  PlaceholderScreen(
    routeName = "Solo 5 minutos",
    onNext = onNext,
    nextLabel = "EMPEZAR 5 MINUTOS",
    onSecondary = onRescueMode,
    secondaryLabel = "Modo Rescate",
    modifier = modifier,
  )
}

@Composable
fun ReadingSessionScreen(onNext: () -> Unit, modifier: Modifier = Modifier) {
  PlaceholderScreen(routeName = "Lectura", onNext = onNext, nextLabel = "Terminar sesión", modifier = modifier)
}
