package com.lectostart.app.reading.data

/**
 * Preguntas de comprensión genéricas (decisión de producto, 2026-09-16): reemplazan a las
 * preguntas autoría por lectura de T-009/T-018. Aplican a cualquier lectura, sembrada o
 * importada por el usuario, para que una lectura importada (sin autor que redacte preguntas)
 * pueda pasar por `QuestionsScreen` igual que las demás. Todas son abiertas
 * (`expectedAnswer == null`) y se autoevalúan, como ya soportaba `QuestionsViewModel`.
 */
private val GENERIC_PROMPTS =
  listOf(
    "¿De qué trata el fragmento de texto que leíste?",
    "¿Qué puedes rescatar como importante?",
    "Elabora un resumen de lo leído",
    "Si le explicaras a alguien lo que comprendiste, ¿qué le dirías?",
    "Rescata 5 palabras o ideas clave que definan el texto.",
    "¿Cuáles fueron las palabras que no comprendiste?",
  )

fun genericQuestionsFor(readingId: String): List<QuestionEntity> =
  GENERIC_PROMPTS.mapIndexed { index, prompt ->
    QuestionEntity(id = "$readingId-generic-q${index + 1}", readingId = readingId, prompt = prompt, type = "generica", expectedAnswer = null)
  }
