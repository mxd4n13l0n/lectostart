package com.lectostart.app.onboarding.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lectostart.app.core.ui.PlaceholderScreen

/** Placeholder de Fase 0 (T-006) pendiente de implementar. UI real: US-03 en docs/USER_STORIES.md. Welcome (US-01) y Consent (US-02) ya son reales: ver WelcomeScreen.kt / ConsentScreen.kt. */
@Composable
fun CreateProfileScreen(onNext: () -> Unit, modifier: Modifier = Modifier) {
  PlaceholderScreen(routeName = "Crear perfil", onNext = onNext, modifier = modifier)
}
