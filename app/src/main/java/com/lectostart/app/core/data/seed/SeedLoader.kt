package com.lectostart.app.core.data.seed

import android.content.Context
import com.lectostart.app.reading.data.QuestionEntity
import com.lectostart.app.reading.data.ReadingEntity
import com.lectostart.app.reading.data.ReadingRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

private const val SEED_FILE_NAME = "seed_readings.json"

/**
 * Precarga las lecturas fijas del MVP (docs/MVP.md §6, decisión 2) desde `assets/seed_readings.json`
 * hacia Room. Idempotente: si ya hay lecturas guardadas, no vuelve a leer el archivo.
 */
class SeedLoader @Inject constructor(@ApplicationContext private val context: Context, private val readingRepository: ReadingRepository) {
  suspend fun seedIfNeeded() {
    if (readingRepository.observeReadings().first().isNotEmpty()) return

    val seedReadings = parseSeedAssets()
    val baseTime = System.currentTimeMillis()

    readingRepository.insertReadings(
      seedReadings.mapIndexed { index, seed ->
        ReadingEntity(id = seed.id, title = seed.title, text = seed.text, source = seed.source, createdAt = baseTime + index)
      }
    )
    readingRepository.insertQuestions(
      seedReadings.flatMap { seed ->
        seed.questions.map { QuestionEntity(id = it.id, readingId = seed.id, prompt = it.prompt, type = it.type, expectedAnswer = it.expectedAnswer) }
      }
    )
  }

  private fun parseSeedAssets(): List<SeedReading> {
    val json = context.assets.open(SEED_FILE_NAME).bufferedReader().use { it.readText() }
    return Json.decodeFromString(json)
  }
}
