package com.lectostart.app.reading.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReadingSessionReducerTest {
  @Test
  fun start_setsRunningTrue() {
    val state = reduceReadingSession(ReadingSessionUiState(), ReadingSessionIntent.Start)
    assertTrue(state.isRunning)
  }

  @Test
  fun tick_whileRunning_incrementsElapsedSec() {
    val running = ReadingSessionUiState(isRunning = true, elapsedSec = 5)
    val state = reduceReadingSession(running, ReadingSessionIntent.Tick)
    assertEquals(6, state.elapsedSec)
  }

  @Test
  fun tick_whileNotRunning_doesNothing() {
    val paused = ReadingSessionUiState(isRunning = false, elapsedSec = 5)
    val state = reduceReadingSession(paused, ReadingSessionIntent.Tick)
    assertEquals(5, state.elapsedSec)
  }

  @Test
  fun pause_setsRunningFalse() {
    val running = ReadingSessionUiState(isRunning = true)
    val state = reduceReadingSession(running, ReadingSessionIntent.Pause)
    assertFalse(state.isRunning)
  }

  @Test
  fun resume_afterPause_setsRunningTrueAgain() {
    val paused = reduceReadingSession(ReadingSessionUiState(isRunning = true), ReadingSessionIntent.Pause)
    val resumed = reduceReadingSession(paused, ReadingSessionIntent.Resume)
    assertTrue(resumed.isRunning)
  }

  @Test
  fun finish_stopsAndMarksFinished() {
    val running = ReadingSessionUiState(isRunning = true, elapsedSec = 120)
    val state = reduceReadingSession(running, ReadingSessionIntent.Finish)
    assertFalse(state.isRunning)
    assertTrue(state.isFinished)
    assertEquals(120, state.elapsedSec) // Finish no descarta el tiempo acumulado
  }

  @Test
  fun abandon_stopsWithoutMarkingFinished() {
    val running = ReadingSessionUiState(isRunning = true, elapsedSec = 42)
    val state = reduceReadingSession(running, ReadingSessionIntent.Abandon)
    assertFalse(state.isRunning)
    assertFalse(state.isFinished)
    assertEquals(42, state.elapsedSec)
  }

  @Test
  fun progress_isElapsedOverDurationClampedTo1() {
    val state = ReadingSessionUiState(durationMin = 5, elapsedSec = 150) // 150s = 2:30 de 5:00
    assertEquals(0.5f, state.progress, 0.001f)

    val overtime = state.copy(elapsedSec = 999)
    assertEquals(1f, overtime.progress, 0.001f)
  }
}
