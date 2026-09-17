package com.lectostart.app.core.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lectostart.app.comprehension.data.AnswerDao
import com.lectostart.app.comprehension.data.AnswerEntity
import com.lectostart.app.onboarding.data.ConsentDao
import com.lectostart.app.onboarding.data.ConsentEntity
import com.lectostart.app.onboarding.data.UserDao
import com.lectostart.app.onboarding.data.UserEntity
import com.lectostart.app.procrastination.data.DiagnosticDao
import com.lectostart.app.procrastination.data.DiagnosticEntity
import com.lectostart.app.reading.data.QuestionDao
import com.lectostart.app.reading.data.QuestionEntity
import com.lectostart.app.reading.data.ReadingDao
import com.lectostart.app.reading.data.ReadingEntity
import com.lectostart.app.reading.data.SessionDao
import com.lectostart.app.reading.data.SessionEntity

/** Base de datos local (Room) de LectoStart. Local-only para el MVP (ver docs/MVP.md §6). */
@Database(
  entities =
    [
      UserEntity::class,
      ConsentEntity::class,
      DiagnosticEntity::class,
      ReadingEntity::class,
      QuestionEntity::class,
      SessionEntity::class,
      AnswerEntity::class,
    ],
  version = 2,
  exportSchema = false,
)
abstract class LectoStartDatabase : RoomDatabase() {
  abstract fun userDao(): UserDao

  abstract fun consentDao(): ConsentDao

  abstract fun diagnosticDao(): DiagnosticDao

  abstract fun readingDao(): ReadingDao

  abstract fun questionDao(): QuestionDao

  abstract fun sessionDao(): SessionDao

  abstract fun answerDao(): AnswerDao
}
