package com.lectostart.app.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Pantalla placeholder usada por el esqueleto de navegación (Fase 0, T-006). Cada feature la
 * envuelve con su nombre de ruta y sus botones; se reemplaza por la UI real conforme se
 * implementa cada historia de docs/USER_STORIES.md.
 */
@Composable
fun PlaceholderScreen(
  routeName: String,
  modifier: Modifier = Modifier,
  onNext: (() -> Unit)? = null,
  nextLabel: String = "Siguiente",
  onSecondary: (() -> Unit)? = null,
  secondaryLabel: String? = null,
) {
  Column(
    modifier = modifier.fillMaxSize().padding(24.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
  ) {
    Text(text = routeName, style = MaterialTheme.typography.headlineSmall)
    if (onNext != null) {
      Button(onClick = onNext) { Text(nextLabel) }
    }
    if (onSecondary != null && secondaryLabel != null) {
      TextButton(onClick = onSecondary) { Text(secondaryLabel) }
    }
  }
}
