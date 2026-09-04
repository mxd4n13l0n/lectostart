package com.lectostart.app.reading.ui

import com.lectostart.app.core.MainDispatcherRule
import com.lectostart.app.reading.data.QuestionEntity
import com.lectostart.app.reading.data.ReadingEntity
import com.lectostart.app.reading.data.ReadingRepository
import com.lectostart.app.reading.data.SessionEntity
import com.lectostart.app.reading.data.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ReadingSessionViewModelTest {
  @get:Rule val mainDispatcherRule = MainDispatcherRule()

  private val reading = ReadingEntity(id = "r1", title = "Lectura de prueba", text = "Texto de prueba", source = null, createdAt = 0L)
  private val session =
    SessionEntity(
      id = "s1",
      userId = "u1",
      readingId = "r1",
      chosenDurationMin = 10,
      actualTimeReadSec = 0,
      completed = false,
      viaRescueMode = false,
      rescueReason = null,
      startedAt = 0L,
      endedAt = null,
    )

  @Test
  fun load_populatesStateAndStartsRunning() = runTest {
    val sessionRepository = FakeSessionRepository2(session)
    val viewModel = ReadingSessionViewModel(FakeReadingRepository(reading), sessionRepository)

    viewModel.load("s1")

    val state = viewModel.uiState.value
    assertFalse(state.isLoading)
    assertEquals("Lectura de prueba", state.readingTitle)
    assertEquals("Texto de prueba", state.readingText)
    assertEquals(10, state.durationMin)
    assertTrue(state.isRunning)

    viewModel.onIntent(ReadingSessionIntent.Finish) // detiene el ticker antes de terminar el test
  }

  @Test
  fun finish_persistsCompletedSession() = runTest {
    val sessionRepository = FakeSessionRepository2(session)
    val viewModel = ReadingSessionViewModel(FakeReadingRepository(reading), sessionRepository)

    viewModel.load("s1")
    viewModel.onIntent(ReadingSessionIntent.Finish)

    assertEquals("s1", sessionRepository.finishedId)
    assertEquals(true, sessionRepository.finishedCompleted)
  }

  @Test
  fun abandon_persistsIncompleteSession() = runTest {
    val sessionRepository = FakeSessionRepository2(session)
    val viewModel = ReadingSessionViewModel(FakeReadingRepository(reading), sessionRepository)

    viewModel.load("s1")
    viewModel.onIntent(ReadingSessionIntent.Abandon)

    assertEquals("s1", sessionRepository.finishedId)
    assertEquals(false, sessionRepository.finishedCompleted)
  }
}

private class FakeReadingRepository(private val reading: ReadingEntity) : ReadingRepository {
  override fun observeReadings(): Flow<List<ReadingEntity>> = flowOf(listOf(reading))

  override suspend fun getReading(id: String): ReadingEntity? = if (id == reading.id) reading else null

  override suspend fun insertReadings(readings: List<ReadingEntity>) = Unit

  override fun observeQuestions(readingId: String): Flow<List<QuestionEntity>> = flowOf(emptyList())

  override suspend fun getQuestions(readingId: String): List<QuestionEntity> = emptyList()

  override suspend fun insertQuestions(questions: List<QuestionEntity>) = Unit
}

private class FakeSessionRepository2(private val session: SessionEntity) : SessionRepository {
  var finishedId: String? = null
  var finishedCompleted: Boolean? = null

  override suspend fun startSession(userId: String, readingId: String, chosenDurationMin: Int, viaRescueMode: Boolean, rescueReason: String?): SessionEntity =
    session

  override suspend fun finishSession(sessionId: String, actualTimeReadSec: Long, completed: Boolean): SessionEntity? {
    finishedId = sessionId
    finishedCompleted = completed
    return session.copy(actualTimeReadSec = actualTimeReadSec, completed = completed)
  }

  override suspend fun getSession(id: String): SessionEntity? = if (id == session.id) session else null

  override fun observeSessionsForUser(userId: String): Flow<List<SessionEntity>> = flowOf(listOf(session))
}
