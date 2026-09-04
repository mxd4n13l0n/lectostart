package com.lectostart.app.core.data.export

import com.lectostart.app.comprehension.data.AnswerEntity
import com.lectostart.app.onboarding.data.UserEntity
import com.lectostart.app.procrastination.data.DiagnosticEntity
import com.lectostart.app.reading.data.SessionEntity
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * DTOs de exportación (docs/ARCHITECTURE.md §8). Copian los campos de cada Entity de Room a un
 * tipo `@Serializable` propio, en vez de anotar las Entities directamente — mantiene la capa de
 * persistencia (Room) separada de la de exportación (JSON), aunque hoy sean casi idénticas.
 */
@Serializable
data class ExportData(
  val exportedAt: Long,
  val users: List<UserExport>,
  val diagnostics: List<DiagnosticExport>,
  val sessions: List<SessionExport>,
  val answers: List<AnswerExport>,
)

@Serializable
data class UserExport(
  val id: String,
  val nickname: String,
  val age: Int,
  val major: String,
  val semester: String,
  val mainGoal: String,
  val createdAt: Long,
)

@Serializable data class DiagnosticExport(val id: Long, val userId: String, val answersJson: String, val level: String, val createdAt: Long)

@Serializable
data class SessionExport(
  val id: String,
  val userId: String,
  val readingId: String,
  val chosenDurationMin: Int,
  val actualTimeReadSec: Long,
  val completed: Boolean,
  val viaRescueMode: Boolean,
  val rescueReason: String?,
  val startedAt: Long,
  val endedAt: Long?,
)

@Serializable data class AnswerExport(val id: Long, val sessionId: String, val questionId: String, val userAnswer: String, val isCorrect: Boolean)

private fun UserEntity.toExport() = UserExport(id, nickname, age, major, semester, mainGoal, createdAt)

private fun DiagnosticEntity.toExport() = DiagnosticExport(id, userId, answersJson, level, createdAt)

private fun SessionEntity.toExport() =
  SessionExport(id, userId, readingId, chosenDurationMin, actualTimeReadSec, completed, viaRescueMode, rescueReason, startedAt, endedAt)

private fun AnswerEntity.toExport() = AnswerExport(id, sessionId, questionId, userAnswer, isCorrect)

private val EXPORT_JSON = Json { prettyPrint = true }

/** Función pura: arma el JSON de exportación a partir de listas de entidades, sin tocar Context ni archivos. */
fun buildExportJson(users: List<UserEntity>, diagnostics: List<DiagnosticEntity>, sessions: List<SessionEntity>, answers: List<AnswerEntity>, exportedAt: Long): String {
  val data =
    ExportData(
      exportedAt = exportedAt,
      users = users.map { it.toExport() },
      diagnostics = diagnostics.map { it.toExport() },
      sessions = sessions.map { it.toExport() },
      answers = answers.map { it.toExport() },
    )
  return EXPORT_JSON.encodeToString(data)
}
