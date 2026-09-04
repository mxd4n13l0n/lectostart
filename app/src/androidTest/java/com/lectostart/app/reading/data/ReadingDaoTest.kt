package com.lectostart.app.reading.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lectostart.app.core.data.LectoStartDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReadingDaoTest {
  private lateinit var db: LectoStartDatabase
  private lateinit var readingDao: ReadingDao
  private lateinit var questionDao: QuestionDao
  private lateinit var sessionDao: SessionDao

  @Before
  fun setup() {
    db =
      Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), LectoStartDatabase::class.java).allowMainThreadQueries().build()
    readingDao = db.readingDao()
    questionDao = db.questionDao()
    sessionDao = db.sessionDao()
  }

  @After
  fun tearDown() {
    db.close()
  }

  @Test
  fun insertAndReadReading() = runTest {
    val reading = ReadingEntity(id = "r1", title = "La importancia de la educación inclusiva", text = "...", source = null, createdAt = 1L)
    readingDao.insert(reading)

    assertEquals(reading, readingDao.getById("r1"))
    assertEquals(listOf(reading), readingDao.observeAll().first())
  }

  @Test
  fun insertAndReadQuestionsForReading() = runTest {
    val question = QuestionEntity(id = "q1", readingId = "r1", prompt = "¿Cuál es la idea principal?", type = "literal", expectedAnswer = null)
    questionDao.insertAll(listOf(question))

    assertEquals(listOf(question), questionDao.getForReading("r1"))
    assertEquals(listOf(question), questionDao.observeForReading("r1").first())
  }

  @Test
  fun insertAndReadSession() = runTest {
    val session =
      SessionEntity(
        id = "s1",
        userId = "u1",
        readingId = "r1",
        chosenDurationMin = 5,
        actualTimeReadSec = 0,
        completed = false,
        viaRescueMode = false,
        rescueReason = null,
        startedAt = 1L,
        endedAt = null,
      )
    sessionDao.insert(session)

    assertEquals(session, sessionDao.getById("s1"))
    assertEquals(listOf(session), sessionDao.observeForUser("u1").first())

    val finished = session.copy(actualTimeReadSec = 300, completed = true, endedAt = 400L)
    sessionDao.update(finished)
    assertEquals(finished, sessionDao.getById("s1"))
  }
}
