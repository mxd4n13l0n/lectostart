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

/** El usuario ya existe en este punto (creado en US-03); el ViewModel lo obtiene de UserRepository, no hace falta threadearlo aquí. */
@Serializable data object Diagnostic : NavKey

@Serializable data class DiagnosticResult(val level: String) : NavKey

@Serializable data object ReadingList : NavKey

/** [readingId] elegido en US-05; viaja por navegación porque no hay un concepto de "lectura actual" persistido (a diferencia del usuario). */
@Serializable data class DurationPicker(val readingId: String) : NavKey

@Serializable data class StartFiveMin(val readingId: String, val durationMin: Int) : NavKey

@Serializable data object ReadingSession : NavKey

@Serializable data object Questions : NavKey

@Serializable data object SessionResult : NavKey

@Serializable data object Progress : NavKey

/** [readingId] viaja para poder seguir hacia StartFiveMin con la misma lectura y duración forzada a 5 min (US-13). */
@Serializable data class RescueMode(val readingId: String) : NavKey
