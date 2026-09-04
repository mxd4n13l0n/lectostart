package com.lectostart.app.onboarding.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ConsentDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(consent: ConsentEntity)

  @Query("SELECT * FROM ConsentEntity WHERE userId = :userId") suspend fun getConsent(userId: String): ConsentEntity?
}
