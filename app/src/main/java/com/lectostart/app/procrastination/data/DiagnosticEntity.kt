package com.lectostart.app.procrastination.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DiagnosticEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val userId: String,
  val answersJson: String, // respuestas Likert serializadas
  val level: String, // Bajo | Intermedio | Alto
  val createdAt: Long,
)
