package com.lectostart.app.comprehension.data

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
class AnswerDaoTest {
  private lateinit var db: LectoStartDatabase
  private lateinit var dao: AnswerDao

  @Before
  fun setup() {
    db =
      Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), LectoStartDatabase::class.java).allowMainThreadQueries().build()
    dao = db.answerDao()
  }

  @After
  fun tearDown() {
    db.close()
  }

  @Test
  fun insertAndReadAnswersForSession() = runTest {
    val answer = AnswerEntity(sessionId = "s1", questionId = "q1", userAnswer = "La idea principal es...", isCorrect = true)
    dao.insertAll(listOf(answer))

    val stored = dao.getForSession("s1")
    assertEquals(listOf(answer.copy(id = stored.first().id)), stored)
    assertEquals(stored, dao.observeForSession("s1").first())
  }
}
