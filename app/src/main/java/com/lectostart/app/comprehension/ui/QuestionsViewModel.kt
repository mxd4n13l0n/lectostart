package com.lectostart.app.comprehension.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lectostart.app.comprehension.data.AnswerEntity
import com.lectostart.app.comprehension.data.ComprehensionRepository
import com.lectostart.app.reading.data.ReadingRepository
import com.lectostart.app.reading.data.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Criterio de corrección (docs/ARCHITECTURE.md §6, decisión pendiente — resuelta aquí):
 * - Pregunta con `expectedAnswer` definido: coincidencia simple (contains, sin distinguir mayúsculas).
 * - Pregunta abierta (`expectedAnswer == null`, el caso de todas las preguntas sembradas en T-009):
 *   autoevaluación del propio participante ("¿Sientes que respondiste bien?" Sí/No).
 */
data class QuestionAnswerState(
  val questionId: String,
  val prompt: String,
  val type: String,
  val expectedAnswer: String?,
  val userAnswer: String = "",
  val selfAssessedCorrect: Boolean? = null,
) {
  val isAnswered: Boolean
    get() = userAnswer.isNotBlank() && (expectedAnswer != null || selfAssessedCorrect != null)
}

data class QuestionsUiState(
  val isLoading: Boolean = true,
  val questions: List<QuestionAnswerState> = emptyList(),
  val validationError: Boolean = false,
  val submitted: Boolean = false,
) {
  val allAnswered: Boolean
    get() = questions.isNotEmpty() && questions.all { it.isAnswered }
}

@HiltViewModel
class QuestionsViewModel @Inject constructor(
  private val readingRepository: ReadingRepository,
  private val sessionRepository: SessionRepository,
  private val comprehensionRepository: ComprehensionRepository,
) : ViewModel() {
  private val _uiState = MutableStateFlow(QuestionsUiState())
  val uiState: StateFlow<QuestionsUiState> = _uiState.asStateFlow()

  private var sessionId: String? = null
  private var loaded = false

  fun load(sessionId: String) {
    if (loaded) return
    loaded = true
    this.sessionId = sessionId
    viewModelScope.launch {
      val session = sessionRepository.getSession(sessionId) ?: return@launch
      val questions = readingRepository.getQuestions(session.readingId)
      _uiState.update {
        it.copy(isLoading = false, questions = questions.map { q -> QuestionAnswerState(q.id, q.prompt, q.type, q.expectedAnswer) })
      }
    }
  }

  fun onAnswerChanged(questionId: String, answer: String) {
    _uiState.update { state ->
      state.copy(
        questions = state.questions.map { if (it.questionId == questionId) it.copy(userAnswer = answer) else it },
        validationError = false,
      )
    }
  }

  fun onSelfAssessed(questionId: String, correct: Boolean) {
    _uiState.update { state ->
      state.copy(
        questions = state.questions.map { if (it.questionId == questionId) it.copy(selfAssessedCorrect = correct) else it },
        validationError = false,
      )
    }
  }

  fun onSubmit() {
    val state = _uiState.value
    if (!state.allAnswered) {
      _uiState.update { it.copy(validationError = true) }
      return
    }
    val id = sessionId ?: return
    viewModelScope.launch {
      val answers =
        state.questions.map { q ->
          val isCorrect =
            if (q.expectedAnswer != null) q.userAnswer.contains(q.expectedAnswer, ignoreCase = true) else q.selfAssessedCorrect == true
          AnswerEntity(sessionId = id, questionId = q.questionId, userAnswer = q.userAnswer, isCorrect = isCorrect)
        }
      comprehensionRepository.saveAnswers(answers)
      _uiState.update { it.copy(submitted = true) }
    }
  }
}
