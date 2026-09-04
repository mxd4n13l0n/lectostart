package com.lectostart.app.progress.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val DATE_FORMATTER = DateTimeFormatter.ofPattern("d MMM, HH:mm")

/** US-11/US-12 (docs/USER_STORIES.md). Pantalla "home" del flujo principal (ver docs/ARCHITECTURE.md §4). */
@Composable
fun ProgressScreen(onStartNewReading: () -> Unit, modifier: Modifier = Modifier, viewModel: ProgressViewModel = hiltViewModel()) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()

  if (state.isLoading) return

  Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
    Text(text = "Mi progreso", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

    val streakText = if (state.streakDays == 1) "🔥 Racha actual: 1 día" else "🔥 Racha actual: ${state.streakDays} días"
    Text(text = streakText, style = MaterialTheme.typography.titleMedium)

    Button(onClick = onStartNewReading, modifier = Modifier.fillMaxWidth()) { Text("Nueva lectura") }

    if (state.sessions.isEmpty()) {
      Text(
        text = "Todavía no has completado ninguna sesión. ¡Empieza tu primera lectura!",
        style = MaterialTheme.typography.bodyMedium,
      )
    } else {
      state.sessions.forEach { session ->
        Card(modifier = Modifier.fillMaxWidth()) {
          Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = session.readingTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = formatDate(session.startedAt), style = MaterialTheme.typography.bodySmall)
            Text(text = "Tiempo leído: ${session.timeReadSec / 60} min ${session.timeReadSec % 60} s", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Comprensión: ${session.comprehensionPercent}%", style = MaterialTheme.typography.bodyMedium)
            if (!session.completed) {
              Text(text = "Sesión no terminada", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            }
          }
        }
      }
    }
  }
}

private fun formatDate(epochMillis: Long): String =
  DATE_FORMATTER.format(Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()))
