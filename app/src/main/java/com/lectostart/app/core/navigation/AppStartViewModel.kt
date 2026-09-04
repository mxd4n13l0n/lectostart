package com.lectostart.app.core.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lectostart.app.onboarding.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/** Determina si la app abre en onboarding o directo en "Mi progreso" (US-01, docs/ARCHITECTURE.md §4). */
sealed interface AppStartDestination {
  data object Loading : AppStartDestination

  data object Onboarding : AppStartDestination

  data object Home : AppStartDestination
}

@HiltViewModel
class AppStartViewModel @Inject constructor(userRepository: UserRepository) : ViewModel() {
  val startDestination: StateFlow<AppStartDestination> =
    userRepository
      .observeUser()
      .map { user -> if (user != null) AppStartDestination.Home else AppStartDestination.Onboarding }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppStartDestination.Loading)
}
