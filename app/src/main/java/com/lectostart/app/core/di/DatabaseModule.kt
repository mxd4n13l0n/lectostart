package com.lectostart.app.core.di

import android.content.Context
import androidx.room.Room
import com.lectostart.app.comprehension.data.AnswerDao
import com.lectostart.app.core.data.LectoStartDatabase
import com.lectostart.app.onboarding.data.ConsentDao
import com.lectostart.app.onboarding.data.UserDao
import com.lectostart.app.procrastination.data.DiagnosticDao
import com.lectostart.app.reading.data.QuestionDao
import com.lectostart.app.reading.data.ReadingDao
import com.lectostart.app.reading.data.SessionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val DATABASE_NAME = "lectostart.db"

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
  @Provides
  @Singleton
  fun provideLectoStartDatabase(@ApplicationContext context: Context): LectoStartDatabase =
    Room.databaseBuilder(context, LectoStartDatabase::class.java, DATABASE_NAME).build()

  @Provides fun provideUserDao(db: LectoStartDatabase): UserDao = db.userDao()

  @Provides fun provideConsentDao(db: LectoStartDatabase): ConsentDao = db.consentDao()

  @Provides fun provideDiagnosticDao(db: LectoStartDatabase): DiagnosticDao = db.diagnosticDao()

  @Provides fun provideReadingDao(db: LectoStartDatabase): ReadingDao = db.readingDao()

  @Provides fun provideQuestionDao(db: LectoStartDatabase): QuestionDao = db.questionDao()

  @Provides fun provideSessionDao(db: LectoStartDatabase): SessionDao = db.sessionDao()

  @Provides fun provideAnswerDao(db: LectoStartDatabase): AnswerDao = db.answerDao()
}
