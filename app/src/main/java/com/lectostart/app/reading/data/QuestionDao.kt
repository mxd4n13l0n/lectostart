package com.lectostart.app.reading.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
  @Insert(onConflict = OnConflictStrategy.IGNORE) suspend fun insertAll(questions: List<QuestionEntity>)

  @Query("SELECT * FROM QuestionEntity WHERE readingId = :readingId") fun observeForReading(readingId: String): Flow<List<QuestionEntity>>

  @Query("SELECT * FROM QuestionEntity WHERE readingId = :readingId") suspend fun getForReading(readingId: String): List<QuestionEntity>
}
