package com.lectostart.app.core.data.seed

import kotlinx.serialization.Serializable

/**
 * Forma del JSON en `assets/seed_readings.json` (docs/ARCHITECTURE.md §6). Ya no trae preguntas
 * propias: desde la decisión de 2026-09-16, todas las lecturas (sembradas o importadas) usan las
 * preguntas genéricas de `GenericQuestions.kt`.
 */
@Serializable data class SeedReading(val id: String, val title: String, val source: String? = null, val text: String)
