package com.lectostart.app.comprehension.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/** US-09 (docs/USER_STORIES.md). Preguntas abiertas se autoevalúan; ver criterio en QuestionsViewModel. */
@Composable
fun QuestionsScreen(sessionId: String, onNext: () -> Unit, modifier: Modifier = Modifier, viewModel: QuestionsViewModel = hiltViewModel()) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(sessionId) { viewModel.load(sessionId) }
  LaunchedEffect(state.submitted) { if (state.submitted) onNext() }

  if (state.isLoading) return

  Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(20.dp)) {
    Text(text = "¿Cuánto comprendiste?", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

    state.questions.forEach { question ->
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = question.prompt, style = MaterialTheme.typography.titleSmall)
        OutlinedTextField(
          value = question.userAnswer,
          onValueChange = { viewModel.onAnswerChanged(question.questionId, it) },
          label = { Text("Tu respuesta") },
          minLines = 2,
          modifier = Modifier.fillMaxWidth(),
        )
        if (question.expectedAnswer == null) {
          Text(text = "¿Sientes que respondiste bien?", style = MaterialTheme.typography.bodySmall)
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = question.selfAssessedCorrect == true, onClick = { viewModel.onSelfAssessed(question.questionId, true) }, label = { Text("Sí") })
            FilterChip(selected = question.selfAssessedCorrect == false, onClick = { viewModel.onSelfAssessed(question.questionId, false) }, label = { Text("No") })
          }
        }
      }
      HorizontalDivider()
    }

    if (state.validationError) {
      Text(text = "Responde todas las preguntas antes de continuar", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
    }

    Button(onClick = viewModel::onSubmit, modifier = Modifier.fillMaxWidth()) { Text("Enviar respuestas") }
  }
}
