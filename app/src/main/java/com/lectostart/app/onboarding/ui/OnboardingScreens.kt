package com.lectostart.app.onboarding.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lectostart.app.core.ui.PlaceholderScreen

/** Placeholders de Fase 0 (T-006) pendientes de implementar. UI real: US-02/US-03 en docs/USER_STORIES.md. WelcomeScreen (US-01) ya es real: ver WelcomeScreen.kt. */
@Composable
fun ConsentScreen(onNext: () -> Unit, modifier: Modifier = Modifier) {
  PlaceholderScreen(routeName = "Consentimiento informado", onNext = onNext, nextLabel = "Aceptar y continuar", modifier = modifier)
}

@Composable
fun CreateProfileScreen(onNext: () -> Unit, modifier: Modifier = Modifier) {
  PlaceholderScreen(routeName = "Crear perfil", onNext = onNext, modifier = modifier)
}
