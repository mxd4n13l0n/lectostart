package com.lectostart.app.reading.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lectostart.app.reading.data.ReadingRepository
import com.lectostart.app.reading.data.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * US-08 (docs/USER_STORIES.md). El reducer puro vive en ReadingSessionState.kt; este ViewModel
 * solo dispara intents y maneja los efectos secundarios (timer, persistencia).
 *
 * Limitación conocida del MVP (US-08, criterio de abandono): la persistencia al cerrar la app se
 * dispara desde ON_STOP del lifecycle (ver ReadingSessionScreen), lo cual es best-effort — no hay
 * garantía de que el `viewModelScope.launch` termine antes de que el proceso muera si Android lo
 * mata inmediatamente después.
 */
@HiltViewModel
class ReadingSessionViewModel @Inject constructor(private val readingRepository: ReadingRepository, private val sessionRepository: SessionRepository) :
  ViewModel() {
  private val _uiState = MutableStateFlow(ReadingSessionUiState())
  val uiState: StateFlow<ReadingSessionUiState> = _uiState.asStateFlow()

  private var tickerJob: Job? = null
  private var sessionId: String? = null
  private var loaded = false

  fun load(sessionId: String) {
    if (loaded) return
    loaded = true
    this.sessionId = sessionId
    viewModelScope.launch {
      val session = sessionRepository.getSession(sessionId) ?: return@launch
      val reading = readingRepository.getReading(session.readingId) ?: return@launch
      _uiState.update { it.copy(isLoading = false, readingTitle = reading.title, readingText = reading.text, durationMin = session.chosenDurationMin) }
      onIntent(ReadingSessionIntent.Start)
    }
  }

  fun onIntent(intent: ReadingSessionIntent) {
    _uiState.update { reduceReadingSession(it, intent) }
    when (intent) {
      ReadingSessionIntent.Start,
      ReadingSessionIntent.Resume -> startTicker()
      ReadingSessionIntent.Pause -> tickerJob?.cancel()
      ReadingSessionIntent.Finish -> {
        tickerJob?.cancel()
        persistCompletion(completed = true)
      }
      ReadingSessionIntent.Abandon -> {
        tickerJob?.cancel()
        persistCompletion(completed = false)
      }
      ReadingSessionIntent.Tick -> Unit
    }
  }

  private fun startTicker() {
    tickerJob?.cancel()
    tickerJob =
      viewModelScope.launch {
        while (true) {
          delay(1_000)
          onIntent(ReadingSessionIntent.Tick)
        }
      }
  }

  private fun persistCompletion(completed: Boolean) {
    val id = sessionId ?: return
    viewModelScope.launch { sessionRepository.finishSession(id, _uiState.value.elapsedSec, completed) }
  }
}
