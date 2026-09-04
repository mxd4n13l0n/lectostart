package com.lectostart.app.core.di

import com.lectostart.app.comprehension.data.ComprehensionRepository
import com.lectostart.app.comprehension.data.ComprehensionRepositoryImpl
import com.lectostart.app.onboarding.data.UserRepository
import com.lectostart.app.onboarding.data.UserRepositoryImpl
import com.lectostart.app.procrastination.data.DiagnosticRepository
import com.lectostart.app.procrastination.data.DiagnosticRepositoryImpl
import com.lectostart.app.reading.data.ReadingRepository
import com.lectostart.app.reading.data.ReadingRepositoryImpl
import com.lectostart.app.reading.data.SessionRepository
import com.lectostart.app.reading.data.SessionRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Bindea cada interfaz XxxRepository a su implementación Room. Es el punto de extensión para un
 * futuro backend remoto (docs/ARCHITECTURE.md §6, decisión 1): cambiar el `@Binds` de este
 * módulo, sin tocar ViewModels ni pantallas.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
  @Binds @Singleton abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

  @Binds @Singleton abstract fun bindDiagnosticRepository(impl: DiagnosticRepositoryImpl): DiagnosticRepository

  @Binds @Singleton abstract fun bindReadingRepository(impl: ReadingRepositoryImpl): ReadingRepository

  @Binds @Singleton abstract fun bindSessionRepository(impl: SessionRepositoryImpl): SessionRepository

  @Binds @Singleton abstract fun bindComprehensionRepository(impl: ComprehensionRepositoryImpl): ComprehensionRepository
}
