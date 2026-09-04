package com.lectostart.app.core.data.export

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.lectostart.app.comprehension.data.ComprehensionRepository
import com.lectostart.app.onboarding.data.UserRepository
import com.lectostart.app.procrastination.data.DiagnosticRepository
import com.lectostart.app.reading.data.SessionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.flow.first

/**
 * Exporta los datos del usuario local a un JSON compartible (docs/ARCHITECTURE.md §8). Es una
 * herramienta para el investigador, no una historia de usuario del participante — se dispara
 * desde una pantalla mínima (T-025).
 */
class DataExporter @Inject constructor(
  @ApplicationContext private val context: Context,
  private val userRepository: UserRepository,
  private val diagnosticRepository: DiagnosticRepository,
  private val sessionRepository: SessionRepository,
  private val comprehensionRepository: ComprehensionRepository,
) {
  /** Genera el archivo en la caché de la app y devuelve un [Uri] listo para compartir vía FileProvider. */
  suspend fun exportToFile(): Uri {
    val user = userRepository.getUser()
    val diagnostics = user?.let { diagnosticRepository.observeForUser(it.id).first() }.orEmpty()
    val sessions = user?.let { sessionRepository.observeSessionsForUser(it.id).first() }.orEmpty()
    val answers = sessions.flatMap { comprehensionRepository.getAnswersForSession(it.id) }

    val json = buildExportJson(users = listOfNotNull(user), diagnostics = diagnostics, sessions = sessions, answers = answers, exportedAt = System.currentTimeMillis())

    val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
    val file = File(exportDir, "lectostart_export_${System.currentTimeMillis()}.json")
    file.writeText(json)

    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
  }
}
