package com.lectostart.app.reading.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val DURATIONS_MIN = listOf(5, 10, 15, 20)

/**
 * US-06 (docs/USER_STORIES.md). Sin ViewModel: es selección de UI pura, sin persistencia propia
 * (la duración se pasa por navegación hacia US-07).
 */
@Composable
fun DurationPickerScreen(onNext: (durationMin: Int) -> Unit, onRescueMode: () -> Unit, modifier: Modifier = Modifier) {
  var selected by remember { mutableStateOf<Int?>(null) }

  Column(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
    Text(text = "¿Cuánto tiempo tienes?", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      DURATIONS_MIN.forEach { minutes ->
        FilterChip(selected = selected == minutes, onClick = { selected = minutes }, label = { Text("$minutes min") })
      }
    }

    Button(onClick = { selected?.let(onNext) }, enabled = selected != null) { Text("Continuar") }
    TextButton(onClick = onRescueMode) { Text("Modo Rescate") }
  }
}
