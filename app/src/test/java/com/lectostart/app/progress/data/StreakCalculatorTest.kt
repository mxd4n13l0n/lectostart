package com.lectostart.app.progress.data

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class StreakCalculatorTest {
  private val today = LocalDate.of(2026, 9, 4)

  @Test
  fun noSessions_streakIsZero() {
    assertEquals(0, StreakCalculator.calculate(emptySet(), today))
  }

  @Test
  fun onlyToday_streakIsOne() {
    assertEquals(1, StreakCalculator.calculate(setOf(today), today))
  }

  @Test
  fun threeConsecutiveDaysEndingToday_streakIsThree() {
    val dates = setOf(today, today.minusDays(1), today.minusDays(2))
    assertEquals(3, StreakCalculator.calculate(dates, today))
  }

  @Test
  fun gapBeforeToday_doesNotExtendPastTheGap() {
    // hoy sí, ayer no, antier sí -> la racha se corta en el hueco de ayer
    val dates = setOf(today, today.minusDays(2))
    assertEquals(1, StreakCalculator.calculate(dates, today))
  }

  @Test
  fun noSessionToday_streakIsZeroEvenIfYesterdayHadOne() {
    // "dejé pasar un día": racha se reinicia a 0 hasta completar una sesión hoy (US-12)
    val dates = setOf(today.minusDays(1), today.minusDays(2))
    assertEquals(0, StreakCalculator.calculate(dates, today))
  }

  @Test
  fun duplicateSessionsSameDay_doNotInflateStreak() {
    // el set de fechas ya deduplica por día; esto solo confirma que 1 día = 1 día de racha
    val dates = setOf(today)
    assertEquals(1, StreakCalculator.calculate(dates, today))
  }
}
