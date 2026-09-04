package com.lectostart.app.reading.data

import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
  suspend fun startSession(userId: String, readingId: String, chosenDurationMin: Int, viaRescueMode: Boolean = false, rescueReason: String? = null): SessionEntity

  suspend fun finishSession(sessionId: String, actualTimeReadSec: Long, completed: Boolean): SessionEntity?

  suspend fun getSession(id: String): SessionEntity?

  fun observeSessionsForUser(userId: String): Flow<List<SessionEntity>>
}

class SessionRepositoryImpl @Inject constructor(private val dao: SessionDao) : SessionRepository {
  override suspend fun startSession(
    userId: String,
    readingId: String,
    chosenDurationMin: Int,
    viaRescueMode: Boolean,
    rescueReason: String?,
  ): SessionEntity {
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
        startedAt = System.currentTimeMillis(),
        endedAt = null,
      )
    dao.insert(session)
    return session
  }

  override suspend fun finishSession(sessionId: String, actualTimeReadSec: Long, completed: Boolean): SessionEntity? {
    val session = dao.getById(sessionId) ?: return null
    val finished = session.copy(actualTimeReadSec = actualTimeReadSec, completed = completed, endedAt = System.currentTimeMillis())
    dao.update(finished)
    return finished
  }

  override suspend fun getSession(id: String): SessionEntity? = dao.getById(id)

  override fun observeSessionsForUser(userId: String): Flow<List<SessionEntity>> = dao.observeForUser(userId)
}
