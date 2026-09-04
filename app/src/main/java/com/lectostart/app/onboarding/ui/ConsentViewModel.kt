package com.lectostart.app.onboarding.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lectostart.app.onboarding.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Versión del texto de consentimiento (docs/PRD.md §10): borrador de producto, pendiente de validación con el comité de ética. */
const val CONSENT_TEXT_VERSION = "consent-mvp-borrador-v1"

sealed interface ConsentStatus {
  data object Pending : ConsentStatus

  data class Accepted(val userId: String) : ConsentStatus

  data object Declined : ConsentStatus
}

@HiltViewModel
class ConsentViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {
  private val _status = MutableStateFlow<ConsentStatus>(ConsentStatus.Pending)
  val status: StateFlow<ConsentStatus> = _status.asStateFlow()

  fun onAccept() {
    viewModelScope.launch {
      val userId = UUID.randomUUID().toString()
      userRepository.recordConsent(userId = userId, textVersion = CONSENT_TEXT_VERSION)
      _status.update { ConsentStatus.Accepted(userId) }
    }
  }

  fun onDecline() {
    _status.update { ConsentStatus.Declined }
  }

  fun onReturnToConsent() {
    _status.update { ConsentStatus.Pending }
  }
}
