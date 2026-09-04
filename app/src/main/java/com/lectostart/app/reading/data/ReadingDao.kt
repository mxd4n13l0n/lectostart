package com.lectostart.app.reading.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingDao {
  @Insert(onConflict = OnConflictStrategy.IGNORE) suspend fun insertAll(readings: List<ReadingEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(reading: ReadingEntity)

  @Query("SELECT * FROM ReadingEntity ORDER BY createdAt") fun observeAll(): Flow<List<ReadingEntity>>

  @Query("SELECT * FROM ReadingEntity WHERE id = :id") suspend fun getById(id: String): ReadingEntity?
}
