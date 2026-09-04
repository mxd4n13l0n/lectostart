package com.lectostart.app.reading.ui

import com.lectostart.app.core.MainDispatcherRule
import com.lectostart.app.onboarding.ui.FakeUserRepository
import com.lectostart.app.reading.data.SessionEntity
import com.lectostart.app.reading.data.SessionRepository
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class StartFiveMinViewModelTest {
  @get:Rule val mainDispatcherRule = MainDispatcherRule()

  @Test
  fun onStart_createsSessionForCurrentUserAndEmitsSessionId() = runTest {
    val userRepository = FakeUserRepository()
    userRepository.createUser("u1", "Dani", 22, "Psicología", "5", "UNDERSTAND_BETTER")
    val sessionRepository = FakeSessionRepository()
    val viewModel = StartFiveMinViewModel(userRepository, sessionRepository)

    viewModel.onStart(readingId = "r1", durationMin = 15, viaRescueMode = false, rescueReason = null)

    assertNotNull(viewModel.sessionId.value)
    assertEquals("u1", sessionRepository.startedFor?.userId)
    assertEquals("r1", sessionRepository.startedFor?.readingId)
    assertEquals(15, sessionRepository.startedFor?.chosenDurationMin)
    assertTrue(!sessionRepository.startedFor!!.viaRescueMode)
  }

  @Test
  fun onStart_viaRescueMode_recordsReason() = runTest {
    val userRepository = FakeUserRepository()
    userRepository.createUser("u1", "Dani", 22, "Psicología", "5", "UNDERSTAND_BETTER")
    val sessionRepository = FakeSessionRepository()
    val viewModel = StartFiveMinViewModel(userRepository, sessionRepository)

    viewModel.onStart(readingId = "r1", durationMin = 5, viaRescueMode = true, rescueReason = "tengo_demasiado_que_leer")

    assertTrue(sessionRepository.startedFor!!.viaRescueMode)
    assertEquals("tengo_demasiado_que_leer", sessionRepository.startedFor?.rescueReason)
  }
}

private class FakeSessionRepository : SessionRepository {
  var startedFor: SessionEntity? = null

  override suspend fun startSession(userId: String, readingId: String, chosenDurationMin: Int, viaRescueMode: Boolean, rescueReason: String?): SessionEntity {
    val session =
      SessionEntity(
        id = UUID.randomUUID().toString(),
        userId = userId,
        readingId = readingId,
        chosenDurationMin = chosenDurationMin,
        actualTimeReadSec = 0,
        completed = false,
        viaRescueMode = viaRescueMode,
        rescueReason = rescueReason,
        startedAt = 0L,
        endedAt = null,
      )
    startedFor = session
    return session
  }

  override suspend fun finishSession(sessionId: String, actualTimeReadSec: Long, completed: Boolean): SessionEntity? = null

  override suspend fun getSession(id: String): SessionEntity? = startedFor

  override fun observeSessionsForUser(userId: String): Flow<List<SessionEntity>> = flowOf(emptyList())
}
