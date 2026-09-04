package com.lectostart.app.comprehension.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lectostart.app.comprehension.data.ComprehensionRepository
import com.lectostart.app.reading.data.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Mensajes motivacionales estáticos (US-10): un set pequeño predefinido, no generados dinámicamente en el MVP. */
val MOTIVATIONAL_MESSAGES =
  listOf(
    "Hoy comenzaste una actividad que estabas posponiendo. ¡Sigue así!",
    "Cada sesión pequeña suma. Vas por buen camino.",
    "Empezar es lo más difícil, y hoy lo lograste.",
  )

data class SessionResultUiState(
  val isLoading: Boolean = true,
  val timeReadSec: Long = 0,
  val comprehensionPercent: Int = 0,
  val correctCount: Int = 0,
  val totalCount: Int = 0,
  val motivationalMessage: String = "",
)

@HiltViewModel
class SessionResultViewModel @Inject constructor(
  private val sessionRepository: SessionRepository,
  private val comprehensionRepository: ComprehensionRepository,
) : ViewModel() {
  private val _uiState = MutableStateFlow(SessionResultUiState())
  val uiState: StateFlow<SessionResultUiState> = _uiState.asStateFlow()

  private var loaded = false

  fun load(sessionId: String) {
    if (loaded) return
    loaded = true
    viewModelScope.launch {
      val session = sessionRepository.getSession(sessionId) ?: return@launch
      val answers = comprehensionRepository.getAnswersForSession(sessionId)
      val correct = answers.count { it.isCorrect }
      val total = answers.size
      val percent = if (total > 0) (correct * 100) / total else 0
      _uiState.update {
        it.copy(
          isLoading = false,
          timeReadSec = session.actualTimeReadSec,
          comprehensionPercent = percent,
          correctCount = correct,
          totalCount = total,
          motivationalMessage = MOTIVATIONAL_MESSAGES.random(),
        )
      }
    }
  }
}
