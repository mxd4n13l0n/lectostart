package com.lectostart.app.onboarding.ui

import com.lectostart.app.core.MainDispatcherRule
import com.lectostart.app.onboarding.data.UserEntity
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class CreateProfileViewModelTest {
  @get:Rule val mainDispatcherRule = MainDispatcherRule()

  @Test
  fun onSubmit_withoutAgeOrMainGoal_showsValidationAndDoesNotCreateUser() = runTest {
    val fakeRepository = FakeUserRepository()
    val viewModel = CreateProfileViewModel(fakeRepository)

    viewModel.onSubmit(userId = "u1")

    val state = viewModel.uiState.value
    assertTrue(state.ageError)
    assertTrue(state.mainGoalError)
    assertFalse(state.profileCreated)
    assertNull(fakeRepository.createdUser)
  }

  @Test
  fun onSubmit_withInvalidAge_showsAgeErrorOnly() = runTest {
    val fakeRepository = FakeUserRepository()
    val viewModel = CreateProfileViewModel(fakeRepository)

    viewModel.onAgeChange("no-es-numero")
    viewModel.onMainGoalSelected(MainGoal.UNDERSTAND_BETTER)
    viewModel.onSubmit(userId = "u1")

    val state = viewModel.uiState.value
    assertTrue(state.ageError)
    assertFalse(state.mainGoalError)
    assertNull(fakeRepository.createdUser)
  }

  @Test
  fun onSubmit_withValidData_createsUserAndMarksProfileCreated() = runTest {
    val fakeRepository = FakeUserRepository()
    val viewModel = CreateProfileViewModel(fakeRepository)

    viewModel.onNicknameChange("Dani")
    viewModel.onAgeChange("22")
    viewModel.onMajorChange("Psicología")
    viewModel.onSemesterChange("5")
    viewModel.onMainGoalSelected(MainGoal.UNDERSTAND_BETTER)
    viewModel.onSubmit(userId = "u1")

    assertTrue(viewModel.uiState.value.profileCreated)
    assertEquals(
      UserEntity(id = "u1", nickname = "Dani", age = 22, major = "Psicología", semester = "5", mainGoal = "UNDERSTAND_BETTER", createdAt = 0L),
      fakeRepository.createdUser,
    )
  }
}
