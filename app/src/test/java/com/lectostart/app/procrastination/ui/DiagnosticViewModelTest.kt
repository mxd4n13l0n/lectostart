package com.lectostart.app.procrastination.ui

import com.lectostart.app.core.MainDispatcherRule
import com.lectostart.app.onboarding.ui.FakeUserRepository
import com.lectostart.app.procrastination.data.DiagnosticEntity
import com.lectostart.app.procrastination.data.DiagnosticLevel
import com.lectostart.app.procrastination.data.DiagnosticRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class DiagnosticViewModelTest {
  @get:Rule val mainDispatcherRule = MainDispatcherRule()

  @Test
  fun onFinishDiagnostic_withIncompleteAnswers_doesNothing() = runTest {
    val userRepository = FakeUserRepository()
    userRepository.createUser("u1", "Dani", 22, "Psicología", "5", "UNDERSTAND_BETTER")
    val diagnosticRepository = FakeDiagnosticRepository()
    val viewModel = DiagnosticViewModel(userRepository, diagnosticRepository)

    viewModel.onAnswerSelected(0, 3) // solo una de 5
    viewModel.onFinishDiagnostic()

    assertFalse(viewModel.uiState.value.allAnswered)
    assertNull(viewModel.uiState.value.savedLevel)
    assertNull(diagnosticRepository.savedFor)
  }

  @Test
  fun onFinishDiagnostic_withAllAnswers_savesAndComputesLevel() = runTest {
    val userRepository = FakeUserRepository()
    userRepository.createUser("u1", "Dani", 22, "Psicología", "5", "UNDERSTAND_BETTER")
    val diagnosticRepository = FakeDiagnosticRepository()
    val viewModel = DiagnosticViewModel(userRepository, diagnosticRepository)

    listOf(5, 5, 5, 5, 5).forEachIndexed { index, value -> viewModel.onAnswerSelected(index, value) }
    viewModel.onFinishDiagnostic()

    assertTrue(viewModel.uiState.value.allAnswered)
    assertEquals(DiagnosticLevel.ALTO, viewModel.uiState.value.savedLevel)
    assertEquals("u1", diagnosticRepository.savedFor)
    assertEquals(listOf(5, 5, 5, 5, 5), diagnosticRepository.savedAnswers)
  }
}

private class FakeDiagnosticRepository : DiagnosticRepository {
  var savedFor: String? = null
  var savedAnswers: List<Int>? = null

  override suspend fun saveDiagnostic(userId: String, answers: List<Int>, level: String): DiagnosticEntity {
    savedFor = userId
    savedAnswers = answers
    return DiagnosticEntity(userId = userId, answersJson = answers.toString(), level = level, createdAt = 0L)
  }

  override suspend fun getLatest(userId: String): DiagnosticEntity? = null

  override fun observeForUser(userId: String): Flow<List<DiagnosticEntity>> = flowOf(emptyList())
}
