package com.lectostart.app.reading.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lectostart.app.onboarding.data.UserRepository
import com.lectostart.app.reading.data.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** El usuario ya existe en este punto del flujo; se obtiene de UserRepository (mismo patrón de T-013). */
@HiltViewModel
class StartFiveMinViewModel @Inject constructor(private val userRepository: UserRepository, private val sessionRepository: SessionRepository) :
  ViewModel() {
  private val _sessionId = MutableStateFlow<String?>(null)
  val sessionId: StateFlow<String?> = _sessionId.asStateFlow()

  fun onStart(readingId: String, durationMin: Int, viaRescueMode: Boolean, rescueReason: String?) {
    viewModelScope.launch {
      val userId = userRepository.getUser()?.id ?: return@launch
      val session = sessionRepository.startSession(userId, readingId, durationMin, viaRescueMode, rescueReason)
      _sessionId.update { session.id }
    }
  }
}
