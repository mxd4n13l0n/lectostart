package com.lectostart.app.reading.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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

/** US-05 (docs/USER_STORIES.md). Lecturas fijas sembradas por T-009 — no hay opción de pegar texto propio en el MVP. */
@Composable
fun ReadingListScreen(onReadingSelected: (readingId: String) -> Unit, modifier: Modifier = Modifier, viewModel: ReadingListViewModel = hiltViewModel()) {
  val readings by viewModel.readings.collectAsStateWithLifecycle()

  Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
    Text(text = "¿Qué quieres leer?", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

    readings.forEach { reading ->
      Card(onClick = { onReadingSelected(reading.id) }, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(text = reading.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
          reading.source?.let { Text(text = it, style = MaterialTheme.typography.bodySmall) }
        }
      }
    }
  }
}
