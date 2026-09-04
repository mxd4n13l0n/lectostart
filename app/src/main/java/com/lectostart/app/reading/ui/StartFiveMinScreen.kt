package com.lectostart.app.reading.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

/** US-07 (docs/USER_STORIES.md). Crea la SesionLectura al tocar el botón de inicio. */
@Composable
fun StartFiveMinScreen(
  readingId: String,
  durationMin: Int,
  viaRescueMode: Boolean,
  rescueReason: String?,
  onNext: (sessionId: String) -> Unit,
  onRescueMode: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: StartFiveMinViewModel = hiltViewModel(),
) {
  val sessionId by viewModel.sessionId.collectAsStateWithLifecycle()

  LaunchedEffect(sessionId) { sessionId?.let(onNext) }

  Column(
    modifier = modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text(text = "Solo 5 minutos", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    Text(
      text = "No tienes que terminar la lectura. Solo empieza durante 5 minutos.",
      style = MaterialTheme.typography.bodyMedium,
      textAlign = TextAlign.Center,
    )
    Button(onClick = { viewModel.onStart(readingId, durationMin, viaRescueMode, rescueReason) }) { Text("EMPEZAR 5 MINUTOS") }
    TextButton(onClick = onRescueMode) { Text("Modo Rescate") }
  }
}
