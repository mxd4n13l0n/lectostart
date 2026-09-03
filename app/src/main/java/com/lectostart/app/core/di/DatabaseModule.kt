package com.lectostart.app.core.di

import android.content.Context
import androidx.room.Room
import com.lectostart.app.core.data.LectoStartDatabase
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
}
