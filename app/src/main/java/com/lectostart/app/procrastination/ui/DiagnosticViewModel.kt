package com.lectostart.app.procrastination.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lectostart.app.procrastination.data.DIAGNOSTIC_QUESTIONS
import com.lectostart.app.procrastination.data.DiagnosticLevel
import com.lectostart.app.procrastination.data.DiagnosticRepository
import com.lectostart.app.procrastination.data.DiagnosticScorer
import com.lectostart.app.onboarding.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DiagnosticUiState(val answers: List<Int?> = List(DIAGNOSTIC_QUESTIONS.size) { null }, val savedLevel: DiagnosticLevel? = null) {
  val allAnswered: Boolean
    get() = answers.all { it != null }
}

/**
 * El usuario ya existe en este punto del flujo (se creó en US-03/T-012), así que se obtiene
 * directo de [UserRepository] — no hace falta threadear el userId por navegación desde aquí en
 * adelante (docs/ARCHITECTURE.md §4).
 */
@HiltViewModel
class DiagnosticViewModel @Inject constructor(private val userRepository: UserRepository, private val diagnosticRepository: DiagnosticRepository) :
  ViewModel() {
  private val _uiState = MutableStateFlow(DiagnosticUiState())
  val uiState: StateFlow<DiagnosticUiState> = _uiState.asStateFlow()

  fun onAnswerSelected(questionIndex: Int, value: Int) {
    _uiState.update { state -> state.copy(answers = state.answers.toMutableList().also { it[questionIndex] = value }) }
  }

  fun onFinishDiagnostic() {
    val state = _uiState.value
    if (!state.allAnswered) return

    viewModelScope.launch {
      val userId = userRepository.getUser()?.id ?: return@launch
      val answers = state.answers.filterNotNull()
      val level = DiagnosticScorer.score(answers)
      diagnosticRepository.saveDiagnostic(userId, answers, level.name)
      _uiState.update { it.copy(savedLevel = level) }
    }
  }
}
