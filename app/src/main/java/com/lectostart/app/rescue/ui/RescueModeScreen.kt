package com.lectostart.app.rescue.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lectostart.app.rescue.data.RescueReason

/** US-13 (docs/USER_STORIES.md). Sin ViewModel: selección de UI pura; la persistencia (viaRescueMode/rescueReason) ocurre al crear la sesión en StartFiveMinViewModel (T-016). */
@Composable
fun RescueModeScreen(onStartFiveMin: (reason: String) -> Unit, modifier: Modifier = Modifier) {
  var selected by remember { mutableStateOf<RescueReason?>(null) }
  val reason = selected

  Column(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
    Text(text = "🚨 Modo Rescate", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

    if (reason == null) {
      Text(text = "¿Qué está pasando?", style = MaterialTheme.typography.titleMedium)
      RescueReason.entries.forEach { option ->
        OutlinedButton(onClick = { selected = option }, modifier = Modifier.fillMaxWidth()) { Text(option.label) }
      }
    } else {
      Text(text = reason.label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
      Text(text = reason.strategy, style = MaterialTheme.typography.bodyLarge)
      Button(onClick = { onStartFiveMin(reason.name) }, modifier = Modifier.fillMaxWidth()) { Text("Comenzar 5 minutos") }
    }
  }
}
