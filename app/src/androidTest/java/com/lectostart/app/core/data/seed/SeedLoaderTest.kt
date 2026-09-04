package com.lectostart.app.core.data.seed

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lectostart.app.reading.data.ReadingRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class SeedLoaderTest {
  @get:Rule val hiltRule = HiltAndroidRule(this)

  @Inject lateinit var seedLoader: SeedLoader
  @Inject lateinit var readingRepository: ReadingRepository

  @Before
  fun setup() {
    hiltRule.inject()
  }

  @Test
  fun seedIfNeeded_populatesReadingsAndQuestionsFromAssets() = runTest {
    seedLoader.seedIfNeeded()

    val readings = readingRepository.observeReadings().first()
    assertEquals(2, readings.size)
    assertTrue(readings.any { it.id == "reading-01" })
    assertTrue(readings.any { it.id == "reading-02" })

    val questions = readingRepository.getQuestions("reading-01")
    assertEquals(4, questions.size)
    assertTrue(questions.any { it.type == "literal" })
    assertTrue(questions.any { it.type == "inferencial" })
    assertTrue(questions.any { it.type == "critica" })
  }

  @Test
  fun seedIfNeeded_isIdempotent() = runTest {
    seedLoader.seedIfNeeded()
    seedLoader.seedIfNeeded() // segunda llamada no debe duplicar ni fallar

    assertEquals(2, readingRepository.observeReadings().first().size)
  }
}
