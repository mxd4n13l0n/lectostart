package com.lectostart.app.onboarding.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/** US-03 (docs/USER_STORIES.md). [userId] viene del consentimiento (US-02, T-011): se genera ahí, no aquí. */
@Composable
fun CreateProfileScreen(userId: String, onNext: () -> Unit, modifier: Modifier = Modifier, viewModel: CreateProfileViewModel = hiltViewModel()) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(state.profileCreated) { if (state.profileCreated) onNext() }

  Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
    Text(text = "Crear perfil", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

    OutlinedTextField(value = state.nickname, onValueChange = viewModel::onNicknameChange, label = { Text("Nombre o apodo") }, modifier = Modifier.fillMaxWidth())

    OutlinedTextField(
      value = state.age,
      onValueChange = viewModel::onAgeChange,
      label = { Text("Edad") },
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      isError = state.ageError,
      supportingText = { if (state.ageError) Text("Ingresa una edad válida") },
      modifier = Modifier.fillMaxWidth(),
    )

    OutlinedTextField(value = state.major, onValueChange = viewModel::onMajorChange, label = { Text("Carrera") }, modifier = Modifier.fillMaxWidth())

    OutlinedTextField(
      value = state.semester,
      onValueChange = viewModel::onSemesterChange,
      label = { Text("Semestre/cuatrimestre") },
      modifier = Modifier.fillMaxWidth(),
    )

    Column {
      Text(text = "¿Qué quieres trabajar?", style = MaterialTheme.typography.titleMedium)
      MainGoal.entries.forEach { goal ->
        val selected = state.mainGoal == goal
        Row(
          modifier =
            Modifier.fillMaxWidth().selectable(selected = selected, onClick = { viewModel.onMainGoalSelected(goal) }, role = Role.RadioButton),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          RadioButton(selected = selected, onClick = null)
          Text(text = goal.label)
        }
      }
      if (state.mainGoalError) {
        Text(text = "Selecciona un objetivo", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
      }
    }

    Button(onClick = { viewModel.onSubmit(userId) }, modifier = Modifier.fillMaxWidth()) { Text("Continuar") }
  }
}
