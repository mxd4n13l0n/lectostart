package com.lectostart.app.reading.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
  @Insert suspend fun insert(session: SessionEntity)

  @Update suspend fun update(session: SessionEntity)

  @Query("SELECT * FROM SessionEntity WHERE userId = :userId ORDER BY startedAt DESC") fun observeForUser(userId: String): Flow<List<SessionEntity>>

  @Query("SELECT * FROM SessionEntity WHERE id = :id") suspend fun getById(id: String): SessionEntity?
}
