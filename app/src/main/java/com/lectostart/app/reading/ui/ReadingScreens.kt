package com.lectostart.app.reading.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lectostart.app.core.ui.PlaceholderScreen

/** Placeholders de Fase 0 (T-006) pendientes. UI real: US-06 a US-08 en docs/USER_STORIES.md. ReadingList (US-05) ya es real: ver ReadingListScreen.kt. */
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
