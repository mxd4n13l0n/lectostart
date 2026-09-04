package com.lectostart.app.core.data.seed

import kotlinx.serialization.Serializable

/** Forma del JSON en `assets/seed_readings.json` (docs/ARCHITECTURE.md §6). */
@Serializable
data class SeedReading(val id: String, val title: String, val source: String? = null, val text: String, val questions: List<SeedQuestion> = emptyList())

@Serializable data class SeedQuestion(val id: String, val prompt: String, val type: String, val expectedAnswer: String? = null)
