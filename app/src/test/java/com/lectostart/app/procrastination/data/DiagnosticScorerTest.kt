package com.lectostart.app.procrastination.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class DiagnosticScorerTest {
  @Test
  fun score_allNunca_isBajo() {
    assertEquals(DiagnosticLevel.BAJO, DiagnosticScorer.score(listOf(1, 1, 1, 1, 1)))
  }

  @Test
  fun score_lowBoundary_isBajo() {
    assertEquals(DiagnosticLevel.BAJO, DiagnosticScorer.score(listOf(3, 2, 2, 2, 2))) // suma 11
  }

  @Test
  fun score_mediumBoundary_isIntermedio() {
    assertEquals(DiagnosticLevel.INTERMEDIO, DiagnosticScorer.score(listOf(3, 3, 2, 2, 2))) // suma 12
  }

  @Test
  fun score_mixedAnswers_isIntermedio() {
    assertEquals(DiagnosticLevel.INTERMEDIO, DiagnosticScorer.score(listOf(3, 3, 3, 3, 3))) // suma 15
  }

  @Test
  fun score_highBoundary_isAlto() {
    assertEquals(DiagnosticLevel.ALTO, DiagnosticScorer.score(listOf(4, 4, 4, 3, 4))) // suma 19
  }

  @Test
  fun score_allSiempre_isAlto() {
    assertEquals(DiagnosticLevel.ALTO, DiagnosticScorer.score(listOf(5, 5, 5, 5, 5)))
  }

  @Test
  fun score_wrongAnswerCount_throws() {
    assertThrows(IllegalArgumentException::class.java) { DiagnosticScorer.score(listOf(1, 2, 3)) }
  }
}
