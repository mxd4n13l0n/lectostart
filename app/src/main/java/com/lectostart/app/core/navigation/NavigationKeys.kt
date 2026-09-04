package com.lectostart.app.core.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Rutas del flujo principal (docs/ARCHITECTURE.md §4). Placeholders de Fase 0: sin argumentos
 * todavía; se agregan (p. ej. sessionId, readingId) cuando cada pantalla se implementa de verdad
 * en las historias de Fase 1 (docs/BACKLOG.md).
 */
@Serializable data object Welcome : NavKey

@Serializable data object Consent : NavKey

/** [userId] se genera al aceptar el consentimiento (US-02) y se reutiliza al crear el perfil (US-03). */
@Serializable data class CreateProfile(val userId: String) : NavKey

@Serializable data object Diagnostic : NavKey

@Serializable data object DiagnosticResult : NavKey

@Serializable data object ReadingList : NavKey

@Serializable data object DurationPicker : NavKey

@Serializable data object StartFiveMin : NavKey

@Serializable data object ReadingSession : NavKey

@Serializable data object Questions : NavKey

@Serializable data object SessionResult : NavKey

@Serializable data object Progress : NavKey

@Serializable data object RescueMode : NavKey
