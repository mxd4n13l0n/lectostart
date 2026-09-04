package com.lectostart.app.onboarding.ui

import app.cash.turbine.test
import com.lectostart.app.onboarding.data.ConsentEntity
import com.lectostart.app.onboarding.data.UserEntity
import com.lectostart.app.onboarding.data.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConsentViewModelTest {
  @Test
  fun onAccept_recordsConsentAndTransitionsToAccepted() = runTest {
    val fakeRepository = FakeUserRepository()
    val viewModel = ConsentViewModel(fakeRepository)

    viewModel.status.test {
      assertEquals(ConsentStatus.Pending, awaitItem())
      viewModel.onAccept()
      val accepted = awaitItem()
      assertTrue(accepted is ConsentStatus.Accepted)
      val userId = (accepted as ConsentStatus.Accepted).userId

      assertTrue(fakeRepository.consentsRecorded.containsKey(userId))
      assertEquals(CONSENT_TEXT_VERSION, fakeRepository.consentsRecorded[userId])
    }
  }

  @Test
  fun onDecline_thenReturn_goesBackToPending() = runTest {
    val viewModel = ConsentViewModel(FakeUserRepository())

    viewModel.status.test {
      assertEquals(ConsentStatus.Pending, awaitItem())
      viewModel.onDecline()
      assertEquals(ConsentStatus.Declined, awaitItem())
      viewModel.onReturnToConsent()
      assertEquals(ConsentStatus.Pending, awaitItem())
    }
  }
}

private class FakeUserRepository : UserRepository {
  val consentsRecorded = mutableMapOf<String, String>()

  override fun observeUser(): Flow<UserEntity?> = MutableStateFlow(null)

  override suspend fun getUser(): UserEntity? = null

  override suspend fun createUser(id: String, nickname: String, age: Int, major: String, semester: String, mainGoal: String): UserEntity =
    UserEntity(id, nickname, age, major, semester, mainGoal, createdAt = 0L)

  override suspend fun getConsent(userId: String): ConsentEntity? = null

  override suspend fun recordConsent(userId: String, textVersion: String) {
    consentsRecorded[userId] = textVersion
  }
}
