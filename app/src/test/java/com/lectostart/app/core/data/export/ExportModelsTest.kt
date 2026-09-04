package com.lectostart.app.core.data.export

import com.lectostart.app.comprehension.data.AnswerEntity
import com.lectostart.app.onboarding.data.UserEntity
import com.lectostart.app.procrastination.data.DiagnosticEntity
import com.lectostart.app.reading.data.SessionEntity
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExportModelsTest {
  @Test
  fun buildExportJson_includesAllEntitiesAndFields() {
    val user = UserEntity(id = "u1", nickname = "Dani", age = 22, major = "Psicología", semester = "5", mainGoal = "UNDERSTAND_BETTER", createdAt = 1L)
    val diagnostic = DiagnosticEntity(id = 1L, userId = "u1", answersJson = "[3,4,2,5,1]", level = "Intermedio", createdAt = 2L)
    val session =
      SessionEntity(
        id = "s1",
        userId = "u1",
        readingId = "r1",
        chosenDurationMin = 5,
        actualTimeReadSec = 120,
        completed = true,
        viaRescueMode = false,
        rescueReason = null,
        startedAt = 3L,
        endedAt = 4L,
      )
    val answer = AnswerEntity(id = 1L, sessionId = "s1", questionId = "q1", userAnswer = "respuesta", isCorrect = true)

    val json = buildExportJson(listOf(user), listOf(diagnostic), listOf(session), listOf(answer), exportedAt = 999L)
    val parsed = Json.decodeFromString(ExportData.serializer(), json)

    assertEquals(999L, parsed.exportedAt)
    assertEquals(1, parsed.users.size)
    assertEquals("u1", parsed.users.first().id)
    assertEquals("Dani", parsed.users.first().nickname)
    assertEquals(1, parsed.diagnostics.size)
    assertEquals("Intermedio", parsed.diagnostics.first().level)
    assertEquals(1, parsed.sessions.size)
    assertTrue(parsed.sessions.first().completed)
    assertEquals(1, parsed.answers.size)
    assertEquals("respuesta", parsed.answers.first().userAnswer)
  }

  @Test
  fun buildExportJson_withNoUser_producesEmptyLists() {
    val json = buildExportJson(emptyList(), emptyList(), emptyList(), emptyList(), exportedAt = 1L)
    val parsed = Json.decodeFromString(ExportData.serializer(), json)

    assertTrue(parsed.users.isEmpty())
    assertTrue(parsed.diagnostics.isEmpty())
    assertTrue(parsed.sessions.isEmpty())
    assertTrue(parsed.answers.isEmpty())
  }
}
