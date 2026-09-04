package com.lectostart.app.comprehension.ui

import com.lectostart.app.comprehension.data.AnswerEntity
import com.lectostart.app.comprehension.data.ComprehensionRepository
import com.lectostart.app.core.MainDispatcherRule
import com.lectostart.app.reading.data.SessionEntity
import com.lectostart.app.reading.data.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

class SessionResultViewModelTest {
  @get:Rule val mainDispatcherRule = MainDispatcherRule()

  private val session =
    SessionEntity(
      id = "s1",
      userId = "u1",
      readingId = "r1",
      chosenDurationMin = 10,
      actualTimeReadSec = 185,
      completed = true,
      viaRescueMode = false,
      rescueReason = null,
      startedAt = 0L,
      endedAt = 185_000L,
    )

  @Test
  fun load_computesComprehensionPercentAndCorrectCount() = runTest {
    val answers =
      listOf(
        AnswerEntity(sessionId = "s1", questionId = "q1", userAnswer = "a", isCorrect = true),
        AnswerEntity(sessionId = "s1", questionId = "q2", userAnswer = "b", isCorrect = true),
        AnswerEntity(sessionId = "s1", questionId = "q3", userAnswer = "c", isCorrect = false),
        AnswerEntity(sessionId = "s1", questionId = "q4", userAnswer = "d", isCorrect = true),
      )
    val viewModel = SessionResultViewModel(FakeSessionRepoForResult(session), FakeComprehensionRepo(answers))

    viewModel.load("s1")

    val state = viewModel.uiState.value
    assertFalse(state.isLoading)
    assertEquals(185L, state.timeReadSec)
    assertEquals(3, state.correctCount)
    assertEquals(4, state.totalCount)
    assertEquals(75, state.comprehensionPercent) // 3/4 = 75%
  }

  @Test
  fun load_withNoAnswers_doesNotDivideByZero() = runTest {
    val viewModel = SessionResultViewModel(FakeSessionRepoForResult(session), FakeComprehensionRepo(emptyList()))

    viewModel.load("s1")

    assertEquals(0, viewModel.uiState.value.comprehensionPercent)
    assertEquals(0, viewModel.uiState.value.totalCount)
  }
}

private class FakeSessionRepoForResult(private val session: SessionEntity) : SessionRepository {
  override suspend fun startSession(userId: String, readingId: String, chosenDurationMin: Int, viaRescueMode: Boolean, rescueReason: String?): SessionEntity =
    session

  override suspend fun finishSession(sessionId: String, actualTimeReadSec: Long, completed: Boolean): SessionEntity? = session

  override suspend fun getSession(id: String): SessionEntity? = if (id == session.id) session else null

  override fun observeSessionsForUser(userId: String): Flow<List<SessionEntity>> = flowOf(listOf(session))
}

private class FakeComprehensionRepo(private val answers: List<AnswerEntity>) : ComprehensionRepository {
  override suspend fun saveAnswers(answers: List<AnswerEntity>) = Unit

  override fun observeAnswersForSession(sessionId: String): Flow<List<AnswerEntity>> = flowOf(answers)

  override suspend fun getAnswersForSession(sessionId: String): List<AnswerEntity> = answers

  override suspend fun comprehensionPercentage(sessionId: String): Float = if (answers.isEmpty()) 0f else answers.count { it.isCorrect }.toFloat() / answers.size
}
