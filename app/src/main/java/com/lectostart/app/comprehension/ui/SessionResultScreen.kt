package com.lectostart.app.comprehension.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/** US-10 (docs/USER_STORIES.md). */
@Composable
fun SessionResultScreen(sessionId: String, onNext: () -> Unit, modifier: Modifier = Modifier, viewModel: SessionResultViewModel = hiltViewModel()) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(sessionId) { viewModel.load(sessionId) }

  if (state.isLoading) return

  Column(
    modifier = modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text(text = "¡Sesión completada!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    Text(text = "Tiempo de lectura: ${formatMinutesSeconds(state.timeReadSec)}", style = MaterialTheme.typography.bodyLarge)
    Text(text = "Comprensión: ${state.comprehensionPercent}%", style = MaterialTheme.typography.bodyLarge)
    Text(text = "Preguntas correctas: ${state.correctCount}/${state.totalCount}", style = MaterialTheme.typography.bodyLarge)
    Text(text = state.motivationalMessage, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
    Button(onClick = onNext) { Text("Continuar mañana") }
  }
}

private fun formatMinutesSeconds(seconds: Long): String {
  val minutes = seconds / 60
  val remaining = seconds % 60
  return if (minutes > 0) "$minutes min $remaining s" else "$remaining s"
}
