package com.lectostart.app.procrastination.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lectostart.app.procrastination.data.DIAGNOSTIC_QUESTIONS
import com.lectostart.app.procrastination.data.LikertAnswer

/** US-04 (docs/USER_STORIES.md). Una sola pantalla con scroll (en vez de una pregunta a la vez). */
@Composable
fun DiagnosticScreen(onNext: (level: String) -> Unit, modifier: Modifier = Modifier, viewModel: DiagnosticViewModel = hiltViewModel()) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(state.savedLevel) { state.savedLevel?.let { onNext(it.name) } }

  Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(24.dp)) {
    Text(text = "Diagnóstico de procrastinación", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    Text(
      text = "Con qué frecuencia te pasa esto cuando tienes una lectura pendiente:",
      style = MaterialTheme.typography.bodyMedium,
    )

    DIAGNOSTIC_QUESTIONS.forEachIndexed { index, question ->
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = question, style = MaterialTheme.typography.titleSmall)
        LikertAnswer.entries.forEach { option ->
          val selected = state.answers[index] == option.value
          Row(
            modifier =
              Modifier.fillMaxWidth().selectable(selected = selected, onClick = { viewModel.onAnswerSelected(index, option.value) }, role = Role.RadioButton),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            RadioButton(selected = selected, onClick = null)
            Text(text = option.label)
          }
        }
      }
      HorizontalDivider()
    }

    Button(onClick = viewModel::onFinishDiagnostic, enabled = state.allAnswered, modifier = Modifier.fillMaxWidth()) {
      Text("Finalizar diagnóstico")
    }
  }
}
