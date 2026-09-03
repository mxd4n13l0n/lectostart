package com.lectostart.app.onboarding.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lectostart.app.core.ui.PlaceholderScreen

/** Placeholders de Fase 0 (T-006). UI real: US-01/US-02/US-03 en docs/USER_STORIES.md. */
@Composable
fun WelcomeScreen(onNext: () -> Unit, modifier: Modifier = Modifier) {
  PlaceholderScreen(routeName = "Bienvenida", onNext = onNext, modifier = modifier)
}

@Composable
fun ConsentScreen(onNext: () -> Unit, modifier: Modifier = Modifier) {
  PlaceholderScreen(routeName = "Consentimiento informado", onNext = onNext, nextLabel = "Aceptar y continuar", modifier = modifier)
}

@Composable
fun CreateProfileScreen(onNext: () -> Unit, modifier: Modifier = Modifier) {
  PlaceholderScreen(routeName = "Crear perfil", onNext = onNext, modifier = modifier)
}
