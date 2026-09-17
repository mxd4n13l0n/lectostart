package com.lectostart.app.reading.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * US-05 (docs/USER_STORIES.md). Lecturas fijas sembradas por T-009. Además de las lecturas
 * fijas, el FAB permite importar una lectura propia (TXT/MD) — adelanto de Fase 4 (T-026,
 * docs/BACKLOG.md), decisión de producto 2026-09-16.
 */
@Composable
fun ReadingListScreen(onReadingSelected: (readingId: String) -> Unit, modifier: Modifier = Modifier, viewModel: ReadingListViewModel = hiltViewModel()) {
  val readings by viewModel.readings.collectAsStateWithLifecycle()
  val snackbarHostState = remember { SnackbarHostState() }

  val filePickerLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri -> uri?.let(viewModel::onFileSelected) }

  LaunchedEffect(Unit) { viewModel.importError.collect { message -> snackbarHostState.showSnackbar(message) } }

  Scaffold(
    modifier = modifier,
    snackbarHost = { SnackbarHost(snackbarHostState) },
    floatingActionButton = {
      FloatingActionButton(onClick = { filePickerLauncher.launch(arrayOf("text/plain", "text/markdown", "application/octet-stream")) }) {
        Text(text = "+", style = MaterialTheme.typography.headlineSmall)
      }
    },
  ) { innerPadding ->
    Column(
      modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
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
}
