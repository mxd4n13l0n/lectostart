package com.lectostart.app.core.data.export

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.lectostart.app.comprehension.data.ComprehensionRepository
import com.lectostart.app.onboarding.data.UserRepository
import com.lectostart.app.procrastination.data.DiagnosticRepository
import com.lectostart.app.reading.data.SessionRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** T-024: verifica el flujo real de exportación (Context, archivo, FileProvider), no solo el JSON. */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class DataExporterTest {
  @get:Rule val hiltRule = HiltAndroidRule(this)

  @Inject lateinit var userRepository: UserRepository
  @Inject lateinit var diagnosticRepository: DiagnosticRepository
  @Inject lateinit var sessionRepository: SessionRepository
  @Inject lateinit var comprehensionRepository: ComprehensionRepository
  @Inject lateinit var dataExporter: DataExporter

  @Before
  fun setup() {
    hiltRule.inject()
  }

  @Test
  fun exportToFile_writesReadableJsonAndReturnsContentUri() = runTest {
    userRepository.recordConsent(userId = "u1", textVersion = "v1")
    userRepository.createUser(id = "u1", nickname = "Dani", age = 22, major = "Psicología", semester = "5", mainGoal = "UNDERSTAND_BETTER")
    diagnosticRepository.saveDiagnostic(userId = "u1", answers = listOf(3, 4, 2, 5, 1), level = "Intermedio")
    val session = sessionRepository.startSession(userId = "u1", readingId = "reading-01", chosenDurationMin = 5)
    sessionRepository.finishSession(session.id, actualTimeReadSec = 120, completed = true)
    comprehensionRepository.saveAnswers(
      listOf(com.lectostart.app.comprehension.data.AnswerEntity(sessionId = session.id, questionId = "q1", userAnswer = "resp", isCorrect = true))
    )

    val uri = dataExporter.exportToFile()

    assertTrue("El URI debe ser un content:// de FileProvider", uri.toString().startsWith("content://"))

    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val content = context.contentResolver.openInputStream(uri)!!.bufferedReader().use { it.readText() }
    val parsed = Json.decodeFromString(ExportData.serializer(), content)

    assertTrue(parsed.users.any { it.id == "u1" })
    assertTrue(parsed.diagnostics.any { it.level == "Intermedio" })
    assertTrue(parsed.sessions.any { it.id == session.id && it.completed })
    assertTrue(parsed.answers.any { it.userAnswer == "resp" })
  }
}
