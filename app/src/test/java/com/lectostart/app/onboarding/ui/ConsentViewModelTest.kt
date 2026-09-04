package com.lectostart.app.onboarding.ui

import app.cash.turbine.test
import com.lectostart.app.core.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ConsentViewModelTest {
  @get:Rule val mainDispatcherRule = MainDispatcherRule()

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
