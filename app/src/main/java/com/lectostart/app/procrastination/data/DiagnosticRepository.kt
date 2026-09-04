package com.lectostart.app.procrastination.data

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface DiagnosticRepository {
  suspend fun saveDiagnostic(userId: String, answers: List<Int>, level: String): DiagnosticEntity

  suspend fun getLatest(userId: String): DiagnosticEntity?

  fun observeForUser(userId: String): Flow<List<DiagnosticEntity>>
}

class DiagnosticRepositoryImpl @Inject constructor(private val dao: DiagnosticDao) : DiagnosticRepository {
  override suspend fun saveDiagnostic(userId: String, answers: List<Int>, level: String): DiagnosticEntity {
    val entity = DiagnosticEntity(userId = userId, answersJson = Json.encodeToString(answers), level = level, createdAt = System.currentTimeMillis())
    val id = dao.insert(entity)
    return entity.copy(id = id)
  }

  override suspend fun getLatest(userId: String): DiagnosticEntity? = dao.getLatestForUser(userId)

  override fun observeForUser(userId: String): Flow<List<DiagnosticEntity>> = dao.observeForUser(userId)
}
