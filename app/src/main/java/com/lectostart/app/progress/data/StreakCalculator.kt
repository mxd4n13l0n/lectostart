package com.lectostart.app.progress.data

import java.time.LocalDate

/**
 * Racha de días consecutivos con al menos una sesión completada (US-12, docs/USER_STORIES.md).
 * Cuenta hacia atrás desde [today]; se detiene en el primer día sin sesión completada — así, si
 * hoy no hay ninguna todavía, la racha es 0 (aunque haya una racha previa rota por un hueco).
 */
object StreakCalculator {
  fun calculate(completedSessionDates: Set<LocalDate>, today: LocalDate = LocalDate.now()): Int {
    var streak = 0
    var date = today
    while (date in completedSessionDates) {
      streak++
      date = date.minusDays(1)
    }
    return streak
  }
}
