package com.lectostart.app.rescue.data

/** 5 motivos + 1 micro-estrategia estática cada uno (US-13, docs/USER_STORIES.md). `name` es lo que se guarda en `SessionEntity.rescueReason`. */
enum class RescueReason(val label: String, val strategy: String) {
  TIRED("Estoy cansado", "No necesitas energía de sobra. Solo abre la lectura y lee una oración. Nada más por ahora."),
  DISTRACTED(
    "Me estoy distrayendo",
    "Guarda el teléfono fuera de tu vista por los próximos 5 minutos. Si te distraes, vuelve a la última oración que recuerdes.",
  ),
  TOO_MUCH_TO_READ("Tengo demasiado que leer", "Divide la lectura en partes pequeñas. Hoy solo trabajaremos las primeras 2 páginas."),
  NO_TIME("No tengo tiempo", "5 minutos caben en cualquier lado. No tienes que terminar, solo empezar."),
  DONT_WANT_TO_START("Simplemente no quiero empezar", "Está bien no tener ganas. No tienes que sentirte motivado para empezar, solo dar el primer paso."),
}
