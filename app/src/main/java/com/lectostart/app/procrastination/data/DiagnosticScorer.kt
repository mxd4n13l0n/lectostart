package com.lectostart.app.procrastination.data

/** Las 5 preguntas Likert del diagnóstico inicial (US-04, docs/USER_STORIES.md). */
val DIAGNOSTIC_QUESTIONS =
  listOf(
    "¿Con qué frecuencia pospones una lectura aunque sabes que tienes que realizarla?",
    "¿Con qué frecuencia piensas \"ya la leeré después\" y ese después nunca llega?",
    "¿Con qué frecuencia te distraes con otras actividades cuando deberías estar leyendo?",
    "¿Con qué frecuencia dejas una lectura para el último momento posible?",
    "¿Con qué frecuencia te sientes culpable por no haber empezado una lectura pendiente?",
  )

enum class LikertAnswer(val value: Int, val label: String) {
  NUNCA(1, "Nunca"),
  CASI_NUNCA(2, "Casi nunca"),
  ALGUNAS_VECES(3, "Algunas veces"),
  CASI_SIEMPRE(4, "Casi siempre"),
  SIEMPRE(5, "Siempre"),
}

enum class DiagnosticLevel(val label: String) {
  BAJO("Bajo"),
  INTERMEDIO("Intermedio"),
  ALTO("Alto"),
}

/**
 * Regla simple y determinista (docs/ARCHITECTURE.md §7). Puntaje = suma de las 5 respuestas
 * Likert (1 a 5 cada una), rango total 5–25 dividido en tercios. Los cortes son un borrador de
 * producto — se ajustan junto con el investigador al construir el cuestionario final.
 */
object DiagnosticScorer {
  private val LOW_RANGE = 5..11
  private val MEDIUM_RANGE = 12..18

  fun score(answers: List<Int>): DiagnosticLevel {
    require(answers.size == DIAGNOSTIC_QUESTIONS.size) { "Se esperaban ${DIAGNOSTIC_QUESTIONS.size} respuestas, llegaron ${answers.size}" }
    val total = answers.sum()
    return when (total) {
      in LOW_RANGE -> DiagnosticLevel.BAJO
      in MEDIUM_RANGE -> DiagnosticLevel.INTERMEDIO
      else -> DiagnosticLevel.ALTO
    }
  }
}
