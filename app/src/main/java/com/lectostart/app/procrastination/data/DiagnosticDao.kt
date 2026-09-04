package com.lectostart.app.procrastination.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DiagnosticDao {
  @Insert suspend fun insert(diagnostic: DiagnosticEntity): Long

  @Query("SELECT * FROM DiagnosticEntity WHERE userId = :userId ORDER BY createdAt DESC LIMIT 1")
  suspend fun getLatestForUser(userId: String): DiagnosticEntity?

  @Query("SELECT * FROM DiagnosticEntity WHERE userId = :userId ORDER BY createdAt DESC")
  fun observeForUser(userId: String): Flow<List<DiagnosticEntity>>
}
