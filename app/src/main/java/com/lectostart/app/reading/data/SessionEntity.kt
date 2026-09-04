package com.lectostart.app.reading.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SessionEntity(
  @PrimaryKey val id: String,
  val userId: String,
  val readingId: String,
  val chosenDurationMin: Int,
  val actualTimeReadSec: Long,
  val completed: Boolean,
  val viaRescueMode: Boolean,
  val rescueReason: String?,
  val startedAt: Long,
  val endedAt: Long?,
)
