package com.lectostart.app.procrastination.data

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
class DiagnosticDaoTest {
  private lateinit var db: LectoStartDatabase
  private lateinit var dao: DiagnosticDao

  @Before
  fun setup() {
    db =
      Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), LectoStartDatabase::class.java).allowMainThreadQueries().build()
    dao = db.diagnosticDao()
  }

  @After
  fun tearDown() {
    db.close()
  }

  @Test
  fun insertAndReadDiagnostic() = runTest {
    val diagnostic = DiagnosticEntity(userId = "u1", answersJson = "[3,4,2,5]", level = "Intermedio", createdAt = 1L)
    val id = dao.insert(diagnostic)

    val stored = dao.getLatestForUser("u1")
    assertEquals(diagnostic.copy(id = id), stored)
    assertEquals(listOf(diagnostic.copy(id = id)), dao.observeForUser("u1").first())
  }
}
