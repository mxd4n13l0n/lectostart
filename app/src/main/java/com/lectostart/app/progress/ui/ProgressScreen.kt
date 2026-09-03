package com.lectostart.app.progress.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lectostart.app.core.ui.PlaceholderScreen

/**
 * Placeholder de Fase 0 (T-006). UI real: US-11/US-12 en docs/USER_STORIES.md. Es la pantalla
 * "home" del flujo; el botón "Reiniciar flujo" es solo para poder recorrer el esqueleto de
 * navegación repetidamente durante Fase 0 y se retira cuando esta pantalla sea real.
 */
@Composable
fun ProgressScreen(onRestartFlow: () -> Unit, modifier: Modifier = Modifier) {
  PlaceholderScreen(routeName = "Mi progreso", onNext = onRestartFlow, nextLabel = "Reiniciar flujo (debug)", modifier = modifier)
}
