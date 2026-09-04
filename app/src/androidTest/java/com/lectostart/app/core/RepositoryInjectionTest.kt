package com.lectostart.app.core

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lectostart.app.comprehension.data.AnswerEntity
import com.lectostart.app.comprehension.data.ComprehensionRepository
import com.lectostart.app.onboarding.data.UserRepository
import com.lectostart.app.procrastination.data.DiagnosticRepository
import com.lectostart.app.reading.data.QuestionEntity
import com.lectostart.app.reading.data.ReadingEntity
import com.lectostart.app.reading.data.ReadingRepository
import com.lectostart.app.reading.data.SessionRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Prueba que los 5 repositorios de docs/ARCHITECTURE.md §6 son inyectables vía Hilt (el mismo
 * mecanismo que usará cualquier @HiltViewModel real, p. ej. ReadingSessionViewModel en T-017) y
 * que cada uno hace un round-trip correcto contra Room. Sirve de base para los tests de
 * ViewModels de Fase 1 (docs/ARCHITECTURE.md §11).
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class RepositoryInjectionTest {
  @get:Rule val hiltRule = HiltAndroidRule(this)

  @Inject lateinit var userRepository: UserRepository
  @Inject lateinit var diagnosticRepository: DiagnosticRepository
  @Inject lateinit var readingRepository: ReadingRepository
  @Inject lateinit var sessionRepository: SessionRepository
  @Inject lateinit var comprehensionRepository: ComprehensionRepository

  @Before
  fun setup() {
    hiltRule.inject()
  }

  @Test
  fun repositoriesAreInjected() {
    assertNotNull(userRepository)
    assertNotNull(diagnosticRepository)
    assertNotNull(readingRepository)
    assertNotNull(sessionRepository)
    assertNotNull(comprehensionRepository)
  }

  @Test
  fun userRepository_createsAndReadsUserAndConsent() = runTest {
    userRepository.recordConsent(userId = "u1", textVersion = "v1")
    val user = userRepository.createUser(id = "u1", nickname = "Dani", age = 22, major = "Psicología", semester = "5", mainGoal = "comprender_mejor")

    assertEquals(user, userRepository.getUser())
    assertEquals(user, userRepository.observeUser().first())
    assertEquals("v1", userRepository.getConsent("u1")?.textVersion)
  }

  @Test
  fun diagnosticRepository_savesAndReadsLatest() = runTest {
    diagnosticRepository.saveDiagnostic(userId = "u1", answers = listOf(3, 4, 2, 5), level = "Intermedio")

    val latest = diagnosticRepository.getLatest("u1")
    assertEquals("Intermedio", latest?.level)
    assertEquals(1, diagnosticRepository.observeForUser("u1").first().size)
  }

  @Test
  fun readingRepository_insertsAndReadsReadingsAndQuestions() = runTest {
    val reading = ReadingEntity(id = "r1", title = "Lectura de prueba", text = "...", source = null, createdAt = 1L)
    readingRepository.insertReadings(listOf(reading))
    val question = QuestionEntity(id = "q1", readingId = "r1", prompt = "¿Idea principal?", type = "literal", expectedAnswer = null)
    readingRepository.insertQuestions(listOf(question))

    assertEquals(reading, readingRepository.getReading("r1"))
    assertEquals(listOf(question), readingRepository.getQuestions("r1"))
  }

  @Test
  fun sessionRepository_startsAndFinishesSession() = runTest {
    val started = sessionRepository.startSession(userId = "u1", readingId = "r1", chosenDurationMin = 5)
    assertTrue(!started.completed)

    val finished = sessionRepository.finishSession(started.id, actualTimeReadSec = 300, completed = true)
    assertEquals(true, finished?.completed)
    assertEquals(300L, finished?.actualTimeReadSec)
    assertEquals(listOf(finished), sessionRepository.observeSessionsForUser("u1").first())
  }

  @Test
  fun comprehensionRepository_savesAnswersAndComputesPercentage() = runTest {
    comprehensionRepository.saveAnswers(
      listOf(
        AnswerEntity(sessionId = "s1", questionId = "q1", userAnswer = "a", isCorrect = true),
        AnswerEntity(sessionId = "s1", questionId = "q2", userAnswer = "b", isCorrect = false),
      )
    )

    assertEquals(2, comprehensionRepository.getAnswersForSession("s1").size)
    assertEquals(0.5f, comprehensionRepository.comprehensionPercentage("s1"))
  }
}
