package com.lectostart.app.onboarding.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lectostart.app.onboarding.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Objetivo principal del participante (US-03, docs/USER_STORIES.md). El nombre del enum es lo que se guarda en `UserEntity.mainGoal`. */
enum class MainGoal(val label: String) {
  START_READING("Comenzar mis lecturas"),
  AVOID_DISTRACTIONS("Evitar distracciones"),
  ORGANIZE_TIME("Organizar mi tiempo"),
  UNDERSTAND_BETTER("Comprender mejor lo que leo"),
}

data class CreateProfileUiState(
  val nickname: String = "",
  val age: String = "",
  val major: String = "",
  val semester: String = "",
  val mainGoal: MainGoal? = null,
  val ageError: Boolean = false,
  val mainGoalError: Boolean = false,
  val profileCreated: Boolean = false,
)

@HiltViewModel
class CreateProfileViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {
  private val _uiState = MutableStateFlow(CreateProfileUiState())
  val uiState: StateFlow<CreateProfileUiState> = _uiState.asStateFlow()

  fun onNicknameChange(value: String) = _uiState.update { it.copy(nickname = value) }

  fun onAgeChange(value: String) = _uiState.update { it.copy(age = value, ageError = false) }

  fun onMajorChange(value: String) = _uiState.update { it.copy(major = value) }

  fun onSemesterChange(value: String) = _uiState.update { it.copy(semester = value) }

  fun onMainGoalSelected(goal: MainGoal) = _uiState.update { it.copy(mainGoal = goal, mainGoalError = false) }

  /** Valida edad y objetivo principal (obligatorios, US-03); si son válidos, crea el perfil con el [userId] generado en el consentimiento (T-011). */
  fun onSubmit(userId: String) {
    val state = _uiState.value
    val age = state.age.toIntOrNull()
    val ageValid = age != null && age > 0
    val mainGoalValid = state.mainGoal != null

    if (!ageValid || !mainGoalValid) {
      _uiState.update { it.copy(ageError = !ageValid, mainGoalError = !mainGoalValid) }
      return
    }

    viewModelScope.launch {
      userRepository.createUser(
        id = userId,
        nickname = state.nickname,
        age = age,
        major = state.major,
        semester = state.semester,
        mainGoal = state.mainGoal.name,
      )
      _uiState.update { it.copy(profileCreated = true) }
    }
  }
}
