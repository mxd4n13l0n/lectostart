package com.lectostart.app.core.navigation

import app.cash.turbine.test
import com.lectostart.app.core.MainDispatcherRule
import com.lectostart.app.onboarding.data.ConsentEntity
import com.lectostart.app.onboarding.data.UserEntity
import com.lectostart.app.onboarding.data.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class AppStartViewModelTest {
  @get:Rule val mainDispatcherRule = MainDispatcherRule()

  @Test
  fun startDestination_isOnboarding_whenNoUserExists() = runTest {
    val viewModel = AppStartViewModel(FakeUserRepository(userFlow = MutableStateFlow(null)))

    viewModel.startDestination.test {
      assertEquals(AppStartDestination.Onboarding, awaitNonLoadingItem())
    }
  }

  @Test
  fun startDestination_isHome_whenUserAlreadyExists() = runTest {
    val existingUser = UserEntity(id = "u1", nickname = "Dani", age = 22, major = "Psicología", semester = "5", mainGoal = "comprender_mejor", createdAt = 1L)
    val viewModel = AppStartViewModel(FakeUserRepository(userFlow = MutableStateFlow(existingUser)))

    viewModel.startDestination.test {
      assertEquals(AppStartDestination.Home, awaitNonLoadingItem())
    }
  }
}

/** Ignora la emisión inicial `Loading` de `stateIn` si llega a aparecer (el orden exacto depende del dispatcher de test usado). */
private suspend fun app.cash.turbine.ReceiveTurbine<AppStartDestination>.awaitNonLoadingItem(): AppStartDestination {
  var item = awaitItem()
  if (item == AppStartDestination.Loading) item = awaitItem()
  return item
}

private class FakeUserRepository(private val userFlow: MutableStateFlow<UserEntity?>) : UserRepository {
  override fun observeUser(): Flow<UserEntity?> = userFlow

  override suspend fun getUser(): UserEntity? = userFlow.value

  override suspend fun createUser(id: String, nickname: String, age: Int, major: String, semester: String, mainGoal: String): UserEntity {
    val user = UserEntity(id, nickname, age, major, semester, mainGoal, createdAt = 0L)
    userFlow.value = user
    return user
  }

  override suspend fun getConsent(userId: String): ConsentEntity? = null

  override suspend fun recordConsent(userId: String, textVersion: String) = Unit

  override suspend fun updateLastCelebratedStreakMilestone(userId: String, milestone: Int) = Unit
}
