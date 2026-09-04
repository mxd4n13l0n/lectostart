package com.lectostart.app.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.lectostart.app.comprehension.ui.QuestionsScreen
import com.lectostart.app.comprehension.ui.SessionResultScreen
import com.lectostart.app.onboarding.ui.ConsentScreen
import com.lectostart.app.onboarding.ui.CreateProfileScreen
import com.lectostart.app.onboarding.ui.WelcomeScreen
import com.lectostart.app.procrastination.ui.DiagnosticResultScreen
import com.lectostart.app.procrastination.ui.DiagnosticScreen
import com.lectostart.app.progress.ui.ProgressScreen
import com.lectostart.app.reading.ui.DurationPickerScreen
import com.lectostart.app.reading.ui.ReadingListScreen
import com.lectostart.app.reading.ui.ReadingSessionScreen
import com.lectostart.app.reading.ui.StartFiveMinScreen
import com.lectostart.app.rescue.ui.RescueModeScreen

/**
 * NavHost central (docs/ARCHITECTURE.md §4). La ruta inicial depende de si ya existe un perfil
 * local: si sí, se salta todo el onboarding y se abre directo en "Mi progreso" (US-01).
 */
@Composable
fun LectoStartNavigation(modifier: Modifier = Modifier, viewModel: AppStartViewModel = hiltViewModel()) {
  when (viewModel.startDestination.collectAsStateWithLifecycle().value) {
    AppStartDestination.Loading -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
    AppStartDestination.Onboarding -> LectoStartNavHost(initialKey = Welcome, modifier = modifier)
    AppStartDestination.Home -> LectoStartNavHost(initialKey = Progress, modifier = modifier)
  }
}

@Composable
private fun LectoStartNavHost(initialKey: NavKey, modifier: Modifier) {
  val backStack = rememberNavBackStack(initialKey)
  val contentModifier = modifier.safeDrawingPadding().padding(16.dp)

  NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryProvider =
      entryProvider {
        entry<Welcome> { WelcomeScreen(onNext = { backStack.add(Consent) }, modifier = contentModifier) }
        entry<Consent> { ConsentScreen(onAccepted = { userId -> backStack.add(CreateProfile(userId)) }, modifier = contentModifier) }
        entry<CreateProfile> { key -> CreateProfileScreen(userId = key.userId, onNext = { backStack.add(Diagnostic) }, modifier = contentModifier) }
        entry<Diagnostic> { DiagnosticScreen(onNext = { level -> backStack.add(DiagnosticResult(level)) }, modifier = contentModifier) }
        entry<DiagnosticResult> { key -> DiagnosticResultScreen(level = key.level, onNext = { backStack.add(ReadingList) }, modifier = contentModifier) }
        entry<ReadingList> { ReadingListScreen(onReadingSelected = { readingId -> backStack.add(DurationPicker(readingId)) }, modifier = contentModifier) }
        entry<DurationPicker> { key ->
          DurationPickerScreen(
            onNext = { durationMin -> backStack.add(StartFiveMin(key.readingId, durationMin)) },
            onRescueMode = { backStack.add(RescueMode(key.readingId)) },
            modifier = contentModifier,
          )
        }
        entry<StartFiveMin> { key ->
          StartFiveMinScreen(
            readingId = key.readingId,
            durationMin = key.durationMin,
            viaRescueMode = key.viaRescueMode,
            rescueReason = key.rescueReason,
            onNext = { sessionId -> backStack.add(ReadingSession(sessionId)) },
            onRescueMode = { backStack.add(RescueMode(key.readingId)) },
            modifier = contentModifier,
          )
        }
        entry<ReadingSession> { key ->
          ReadingSessionScreen(sessionId = key.sessionId, onNext = { backStack.add(Questions(key.sessionId)) }, modifier = contentModifier)
        }
        entry<Questions> { QuestionsScreen(onNext = { backStack.add(SessionResult) }, modifier = contentModifier) }
        entry<SessionResult> { SessionResultScreen(onNext = { backStack.add(Progress) }, modifier = contentModifier) }
        entry<Progress> {
          ProgressScreen(
            onRestartFlow = {
              backStack.clear()
              backStack.add(Welcome)
            },
            modifier = contentModifier,
          )
        }
        entry<RescueMode> { key ->
          RescueModeScreen(
            onStartFiveMin = {
              backStack.removeLastOrNull() // sale de RescueMode
              backStack.add(StartFiveMin(key.readingId, durationMin = 5, viaRescueMode = true)) // Modo Rescate siempre fuerza 5 min (US-13)
            },
            modifier = contentModifier,
          )
        }
      },
  )
}
