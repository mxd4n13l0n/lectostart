package com.lectostart.app.onboarding.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(user: UserEntity)

  // La app es de un solo usuario por dispositivo (local-only, ver docs/MVP.md §6): basta con el primer registro.
  @Query("SELECT * FROM UserEntity LIMIT 1") fun observeUser(): Flow<UserEntity?>

  @Query("SELECT * FROM UserEntity LIMIT 1") suspend fun getUser(): UserEntity?
}
