package com.lectostart.app.comprehension.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AnswerDao {
  @Insert suspend fun insertAll(answers: List<AnswerEntity>)

  @Query("SELECT * FROM AnswerEntity WHERE sessionId = :sessionId") fun observeForSession(sessionId: String): Flow<List<AnswerEntity>>

  @Query("SELECT * FROM AnswerEntity WHERE sessionId = :sessionId") suspend fun getForSession(sessionId: String): List<AnswerEntity>
}
