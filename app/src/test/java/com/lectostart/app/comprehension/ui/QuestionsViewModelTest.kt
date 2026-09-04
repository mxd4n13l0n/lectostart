package com.lectostart.app.comprehension.ui

import com.lectostart.app.comprehension.data.AnswerEntity
import com.lectostart.app.comprehension.data.ComprehensionRepository
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

class QuestionsViewModelTest {
  @get:Rule val mainDispatcherRule = MainDispatcherRule()

  private val session =
    SessionEntity(
      id = "s1",
      userId = "u1",
      readingId = "r1",
      chosenDurationMin = 5,
      actualTimeReadSec = 60,
      completed = true,
      viaRescueMode = false,
      rescueReason = null,
      startedAt = 0L,
      endedAt = 60_000L,
    )

  private val openQuestion = QuestionEntity(id = "q1", readingId = "r1", prompt = "¿Idea principal?", type = "literal", expectedAnswer = null)
  private val closedQuestion = QuestionEntity(id = "q2", readingId = "r1", prompt = "¿Autor menciona X?", type = "literal", expectedAnswer = "aprendizaje activo")

  @Test
  fun onSubmit_withUnansweredQuestions_showsValidationErrorAndDoesNotSave() = runTest {
    val comprehensionRepository = FakeComprehensionRepository()
    val viewModel = QuestionsViewModel(FakeReadingRepo(listOf(openQuestion)), FakeSessionRepo(session), comprehensionRepository)

    viewModel.load("s1")
    viewModel.onSubmit()

    assertTrue(viewModel.uiState.value.validationError)
    assertFalse(viewModel.uiState.value.submitted)
    assertEquals(null, comprehensionRepository.saved)
  }

  @Test
  fun onSubmit_openQuestion_usesSelfAssessment() = runTest {
    val comprehensionRepository = FakeComprehensionRepository()
    val viewModel = QuestionsViewModel(FakeReadingRepo(listOf(openQuestion)), FakeSessionRepo(session), comprehensionRepository)

    viewModel.load("s1")
    viewModel.onAnswerChanged("q1", "La idea principal es...")
    viewModel.onSelfAssessed("q1", true)
    viewModel.onSubmit()

    assertTrue(viewModel.uiState.value.submitted)
    assertEquals(1, comprehensionRepository.saved?.size)
    assertTrue(comprehensionRepository.saved!!.first().isCorrect)
  }

  @Test
  fun onSubmit_closedQuestion_evaluatesAgainstExpectedAnswer() = runTest {
    val comprehensionRepository = FakeComprehensionRepository()
    val viewModel = QuestionsViewModel(FakeReadingRepo(listOf(closedQuestion)), FakeSessionRepo(session), comprehensionRepository)

    viewModel.load("s1")
    viewModel.onAnswerChanged("q2", "El texto habla de aprendizaje activo en clase")
    viewModel.onSubmit()

    assertTrue(viewModel.uiState.value.submitted)
    assertTrue(comprehensionRepository.saved!!.first().isCorrect)
  }

  @Test
  fun onSubmit_closedQuestion_wrongAnswer_isMarkedIncorrect() = runTest {
    val comprehensionRepository = FakeComprehensionRepository()
    val viewModel = QuestionsViewModel(FakeReadingRepo(listOf(closedQuestion)), FakeSessionRepo(session), comprehensionRepository)

    viewModel.load("s1")
    viewModel.onAnswerChanged("q2", "No estoy seguro")
    viewModel.onSubmit()

    assertTrue(viewModel.uiState.value.submitted)
    assertFalse(comprehensionRepository.saved!!.first().isCorrect)
  }
}

private class FakeReadingRepo(private val questions: List<QuestionEntity>) : ReadingRepository {
  override fun observeReadings(): Flow<List<ReadingEntity>> = flowOf(emptyList())

  override suspend fun getReading(id: String): ReadingEntity? = null

  override suspend fun insertReadings(readings: List<ReadingEntity>) = Unit

  override fun observeQuestions(readingId: String): Flow<List<QuestionEntity>> = flowOf(questions)

  override suspend fun getQuestions(readingId: String): List<QuestionEntity> = questions

  override suspend fun insertQuestions(questions: List<QuestionEntity>) = Unit
}

private class FakeSessionRepo(private val session: SessionEntity) : SessionRepository {
  override suspend fun startSession(userId: String, readingId: String, chosenDurationMin: Int, viaRescueMode: Boolean, rescueReason: String?): SessionEntity =
    session

  override suspend fun finishSession(sessionId: String, actualTimeReadSec: Long, completed: Boolean): SessionEntity? = session

  override suspend fun getSession(id: String): SessionEntity? = if (id == session.id) session else null

  override fun observeSessionsForUser(userId: String): Flow<List<SessionEntity>> = flowOf(listOf(session))
}

private class FakeComprehensionRepository : ComprehensionRepository {
  var saved: List<AnswerEntity>? = null

  override suspend fun saveAnswers(answers: List<AnswerEntity>) {
    saved = answers
  }

  override fun observeAnswersForSession(sessionId: String): Flow<List<AnswerEntity>> = flowOf(saved.orEmpty())

  override suspend fun getAnswersForSession(sessionId: String): List<AnswerEntity> = saved.orEmpty()

  override suspend fun comprehensionPercentage(sessionId: String): Float = 0f
}
