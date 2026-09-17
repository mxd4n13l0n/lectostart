package com.lectostart.app.progress.ui

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lectostart.app.progress.data.STREAK_MILESTONES
import com.lectostart.app.progress.data.streakEmoji
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val DATE_FORMATTER = DateTimeFormatter.ofPattern("d MMM, HH:mm")

/** US-11/US-12 (docs/USER_STORIES.md). Pantalla "home" del flujo principal (ver docs/ARCHITECTURE.md §4). */
@Composable
fun ProgressScreen(onStartNewReading: () -> Unit, modifier: Modifier = Modifier, viewModel: ProgressViewModel = hiltViewModel()) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  val context = LocalContext.current
  val snackbarHostState = remember { SnackbarHostState() }

  LaunchedEffect(viewModel) {
    viewModel.exportEvent.collect { uri ->
      val shareIntent =
        Intent(Intent.ACTION_SEND).apply {
          type = "application/json"
          putExtra(Intent.EXTRA_STREAM, uri)
          addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
      context.startActivity(Intent.createChooser(shareIntent, "Exportar datos de LectoStart"))
    }
  }

  LaunchedEffect(viewModel) { viewModel.streakCelebration.collect { milestone -> snackbarHostState.showSnackbar("${milestone.emoji} ${milestone.celebration}") } }

  if (state.isLoading) return

  Scaffold(modifier = modifier, snackbarHost = { SnackbarHost(snackbarHostState) }) { innerPadding ->
    Column(
      modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Text(text = "Mi progreso", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

      val emoji = streakEmoji(state.streakDays)
      val streakText = if (state.streakDays == 1) "$emoji Racha actual: 1 día" else "$emoji Racha actual: ${state.streakDays} días"
      Text(text = streakText, style = MaterialTheme.typography.titleMedium)

      StreakBadgeRow(streakDays = state.streakDays)

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

      // Herramienta de investigador (T-025), no una historia de usuario del participante.
      TextButton(onClick = viewModel::onExportRequested) { Text("Exportar datos (investigador)") }
    }
  }
}

/** Idea 1 del brainstorming (2026-09-16): insignias por hito de racha, coloreadas si ya se alcanzaron. */
@Composable
private fun StreakBadgeRow(streakDays: Int) {
  Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
    STREAK_MILESTONES.forEach { milestone ->
      val unlocked = streakDays >= milestone.days
      Surface(
        color = if (unlocked) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        contentColor = if (unlocked) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
        shape = MaterialTheme.shapes.small,
      ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
          Text(text = milestone.emoji, style = MaterialTheme.typography.titleMedium)
          Text(text = milestone.label, style = MaterialTheme.typography.labelSmall)
        }
      }
    }
  }
}

private fun formatDate(epochMillis: Long): String =
  DATE_FORMATTER.format(Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()))
