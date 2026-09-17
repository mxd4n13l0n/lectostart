package com.lectostart.app.reading.data

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import java.util.UUID
import javax.inject.Inject

private val SUPPORTED_EXTENSIONS = setOf("txt", "md")

/**
 * Carga una lectura propia del participante desde un archivo local (adelanto de Fase 4,
 * docs/BACKLOG.md T-026, decisión de producto 2026-09-16). Solo TXT/MD en esta primera versión:
 * se leen como texto plano, sin extracción de formato ni de PDF.
 */
class ReadingImporter @Inject constructor(@ApplicationContext private val context: Context, private val readingRepository: ReadingRepository) {
  sealed interface Result {
    data class Success(val readingId: String) : Result

    data class Error(val message: String) : Result
  }

  suspend fun import(uri: Uri): Result {
    val displayName = queryDisplayName(uri)
    val extension = displayName?.substringAfterLast('.', missingDelimiterValue = "")?.lowercase()
    if (extension !in SUPPORTED_EXTENSIONS) {
      return Result.Error("Solo se admiten archivos .txt o .md")
    }

    val text =
      try {
        context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
      } catch (_: IOException) {
        null
      } ?: return Result.Error("No se pudo leer el archivo")

    if (text.isBlank()) return Result.Error("El archivo está vacío")

    val readingId = UUID.randomUUID().toString()
    val title = displayName?.substringBeforeLast('.')?.takeIf { it.isNotBlank() } ?: "Lectura importada"

    readingRepository.insertReadings(
      listOf(ReadingEntity(id = readingId, title = title, text = text.trim(), source = "Importado desde tu dispositivo", createdAt = System.currentTimeMillis()))
    )
    readingRepository.insertQuestions(genericQuestionsFor(readingId))

    return Result.Success(readingId)
  }

  private fun queryDisplayName(uri: Uri): String? =
    context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
      val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
      if (nameIndex == -1 || !cursor.moveToFirst()) null else cursor.getString(nameIndex)
    }
}
