package com.lectostart.app.comprehension.data

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

interface ComprehensionRepository {
  suspend fun saveAnswers(answers: List<AnswerEntity>)

  fun observeAnswersForSession(sessionId: String): Flow<List<AnswerEntity>>

  suspend fun getAnswersForSession(sessionId: String): List<AnswerEntity>

  /** % de respuestas correctas de la sesión (0f si no hay respuestas). Usado por US-10. */
  suspend fun comprehensionPercentage(sessionId: String): Float
}

class ComprehensionRepositoryImpl @Inject constructor(private val dao: AnswerDao) : ComprehensionRepository {
  override suspend fun saveAnswers(answers: List<AnswerEntity>) = dao.insertAll(answers)

  override fun observeAnswersForSession(sessionId: String): Flow<List<AnswerEntity>> = dao.observeForSession(sessionId)

  override suspend fun getAnswersForSession(sessionId: String): List<AnswerEntity> = dao.getForSession(sessionId)

  override suspend fun comprehensionPercentage(sessionId: String): Float {
    val answers = dao.getForSession(sessionId)
    if (answers.isEmpty()) return 0f
    return answers.count { it.isCorrect }.toFloat() / answers.size
  }
}
