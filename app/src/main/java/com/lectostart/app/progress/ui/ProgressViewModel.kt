package com.lectostart.app.progress.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lectostart.app.comprehension.data.ComprehensionRepository
import com.lectostart.app.onboarding.data.UserRepository
import com.lectostart.app.reading.data.ReadingRepository
import com.lectostart.app.reading.data.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SessionHistoryItem(
  val sessionId: String,
  val readingTitle: String,
  val startedAt: Long,
  val timeReadSec: Long,
  val completed: Boolean,
  val comprehensionPercent: Int,
)

data class ProgressUiState(val isLoading: Boolean = true, val sessions: List<SessionHistoryItem> = emptyList())

/** US-11 (docs/USER_STORIES.md). `SessionRepository.observeSessionsForUser` ya ordena por `startedAt DESC` (T-007). */
@HiltViewModel
class ProgressViewModel @Inject constructor(
  private val userRepository: UserRepository,
  private val sessionRepository: SessionRepository,
  private val readingRepository: ReadingRepository,
  private val comprehensionRepository: ComprehensionRepository,
) : ViewModel() {
  private val _uiState = MutableStateFlow(ProgressUiState())
  val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()

  init {
    viewModelScope.launch {
      val user = userRepository.getUser() ?: return@launch
      sessionRepository.observeSessionsForUser(user.id).collectLatest { sessions ->
        val items =
          sessions.map { session ->
            val reading = readingRepository.getReading(session.readingId)
            val percent = (comprehensionRepository.comprehensionPercentage(session.id) * 100).toInt()
            SessionHistoryItem(
              sessionId = session.id,
              readingTitle = reading?.title ?: "Lectura",
              startedAt = session.startedAt,
              timeReadSec = session.actualTimeReadSec,
              completed = session.completed,
              comprehensionPercent = percent,
            )
          }
        _uiState.update { it.copy(isLoading = false, sessions = items) }
      }
    }
  }
}
