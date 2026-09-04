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
import kotlinx.coroutines.flow.take

/** Determina si la app abre en onboarding o directo en "Mi progreso" (US-01, docs/ARCHITECTURE.md §4). */
sealed interface AppStartDestination {
  data object Loading : AppStartDestination

  data object Onboarding : AppStartDestination

  data object Home : AppStartDestination
}

@HiltViewModel
class AppStartViewModel @Inject constructor(userRepository: UserRepository) : ViewModel() {
  /**
   * Se evalúa una sola vez, al arrancar (`take(1)`). Debe ser una decisión de "cold start", no
   * reactiva: si se siguiera observando `observeUser()` durante toda la sesión, crear el perfil
   * a mitad del onboarding (US-03, T-012) haría que la app saltara de golpe a "Mi progreso",
   * abandonando el resto del flujo (diagnóstico, lectura, etc.) — bug real encontrado en T-012.
   */
  val startDestination: StateFlow<AppStartDestination> =
    userRepository
      .observeUser()
      .take(1)
      .map { user -> if (user != null) AppStartDestination.Home else AppStartDestination.Onboarding }
      .stateIn(viewModelScope, SharingStarted.Eagerly, AppStartDestination.Loading)
}
