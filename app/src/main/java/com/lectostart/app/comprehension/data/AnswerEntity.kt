package com.lectostart.app.comprehension.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AnswerEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val sessionId: String,
  val questionId: String,
  val userAnswer: String,
  val isCorrect: Boolean,
)
