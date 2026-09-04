package com.lectostart.app.reading.ui

/** MVI acotado a esta pantalla (docs/ARCHITECTURE.md §10.1) — es la única con una máquina de estados real (timer, pausa, abandono). */
sealed interface ReadingSessionIntent {
  data object Start : ReadingSessionIntent

  data object Tick : ReadingSessionIntent

  data object Pause : ReadingSessionIntent

  data object Resume : ReadingSessionIntent

  data object Finish : ReadingSessionIntent

  data object Abandon : ReadingSessionIntent
}

data class ReadingSessionUiState(
  val isLoading: Boolean = true,
  val readingTitle: String = "",
  val readingText: String = "",
  val durationMin: Int = 5,
  val elapsedSec: Long = 0,
  val isRunning: Boolean = false,
  val isFinished: Boolean = false,
) {
  /** Progreso basado en tiempo transcurrido vs. duración elegida (decisión de UX de ARCHITECTURE.md §12, resuelta aquí: tiempo, no scroll). */
  val progress: Float
    get() = (elapsedSec.toFloat() / (durationMin * 60).coerceAtLeast(1)).coerceIn(0f, 1f)
}

/** Reducer puro, testeable sin Android ni mocks (docs/ARCHITECTURE.md §10.1). */
fun reduceReadingSession(state: ReadingSessionUiState, intent: ReadingSessionIntent): ReadingSessionUiState =
  when (intent) {
    ReadingSessionIntent.Start -> state.copy(isRunning = true)
    ReadingSessionIntent.Tick -> if (state.isRunning) state.copy(elapsedSec = state.elapsedSec + 1) else state
    ReadingSessionIntent.Pause -> state.copy(isRunning = false)
    ReadingSessionIntent.Resume -> state.copy(isRunning = true)
    ReadingSessionIntent.Finish -> state.copy(isRunning = false, isFinished = true)
    ReadingSessionIntent.Abandon -> state.copy(isRunning = false)
  }
