package com.lectostart.app.reading.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lectostart.app.core.ui.PlaceholderScreen

/** Placeholder de Fase 0 (T-006) pendiente. UI real: US-08 en docs/USER_STORIES.md. El resto de reading/ui ya es real. */
@Composable
fun ReadingSessionScreen(onNext: () -> Unit, modifier: Modifier = Modifier) {
  PlaceholderScreen(routeName = "Lectura", onNext = onNext, nextLabel = "Terminar sesión", modifier = modifier)
}
