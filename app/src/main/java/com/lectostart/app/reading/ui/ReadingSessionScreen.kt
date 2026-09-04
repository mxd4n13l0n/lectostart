package com.lectostart.app.reading.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle

private const val READING_TIP = "Si te distraes, regresa a la última oración que recuerdes y continúa desde ahí."

/** US-08 (docs/USER_STORIES.md). Detecta abandono (cerrar la app) observando ON_STOP del lifecycle. */
@Composable
fun ReadingSessionScreen(sessionId: String, onNext: () -> Unit, modifier: Modifier = Modifier, viewModel: ReadingSessionViewModel = hiltViewModel()) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(sessionId) { viewModel.load(sessionId) }
  LaunchedEffect(state.isFinished) { if (state.isFinished) onNext() }

  val lifecycleOwner = LocalLifecycleOwner.current
  DisposableEffect(lifecycleOwner, viewModel) {
    val observer = LifecycleEventObserver { _, event ->
      if (event == Lifecycle.Event.ON_STOP && !viewModel.uiState.value.isFinished) {
        viewModel.onIntent(ReadingSessionIntent.Abandon)
      }
    }
    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
  }

  if (state.isLoading) return

  Column(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
    Text(text = state.readingTitle, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

    LinearProgressIndicator(progress = { state.progress }, modifier = Modifier.fillMaxWidth())
    Text(text = formatElapsed(state.elapsedSec), style = MaterialTheme.typography.bodyMedium)

    Box(modifier = Modifier.weight(1f)) {
      Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) { Text(text = state.readingText, style = MaterialTheme.typography.bodyLarge) }
    }

    Text(text = READING_TIP, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Normal)

    Button(onClick = { viewModel.onIntent(ReadingSessionIntent.Finish) }, modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally)) {
      Text("Terminar sesión")
    }
  }
}

private fun formatElapsed(seconds: Long): String {
  val minutes = seconds / 60
  val remainingSeconds = seconds % 60
  return "%02d:%02d".format(minutes, remainingSeconds)
}
