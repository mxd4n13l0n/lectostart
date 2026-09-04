package com.lectostart.app.onboarding.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserEntity(
  @PrimaryKey val id: String, // UUID generado en el dispositivo
  val nickname: String,
  val age: Int,
  val major: String,
  val semester: String,
  val mainGoal: String, // enum serializado como String
  val createdAt: Long,
)
