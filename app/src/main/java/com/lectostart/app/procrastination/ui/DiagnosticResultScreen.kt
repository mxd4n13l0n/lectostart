package com.lectostart.app.procrastination.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lectostart.app.procrastination.data.DiagnosticLevel

/** US-04 (docs/USER_STORIES.md). [level] es el nombre del enum [DiagnosticLevel] calculado en DiagnosticScreen. */
@Composable
fun DiagnosticResultScreen(level: String, onNext: () -> Unit, modifier: Modifier = Modifier) {
  val label = runCatching { DiagnosticLevel.valueOf(level).label }.getOrDefault(level)

  Column(
    modifier = modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text(text = "Tu punto de partida", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    Text(text = "Nivel de inicio de lectura: $label", style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
    Text(
      text = "No necesitas terminar todo de una vez. Vamos a comenzar con 5 minutos.",
      style = MaterialTheme.typography.bodyMedium,
      textAlign = TextAlign.Center,
    )
    Button(onClick = onNext) { Text("Continuar") }
  }
}
