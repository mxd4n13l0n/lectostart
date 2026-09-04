package com.lectostart.app.reading.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class QuestionEntity(
  @PrimaryKey val id: String,
  val readingId: String,
  val prompt: String,
  val type: String, // literal | inferencial | critica
  val expectedAnswer: String?, // criterio de corrección
)
