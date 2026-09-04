package com.lectostart.app.reading.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lectostart.app.core.ui.PlaceholderScreen

/** Placeholders de Fase 0 (T-006) pendientes. UI real: US-07/US-08 en docs/USER_STORIES.md. ReadingList (US-05) y DurationPicker (US-06) ya son reales: ver ReadingListScreen.kt / DurationPickerScreen.kt. */
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
