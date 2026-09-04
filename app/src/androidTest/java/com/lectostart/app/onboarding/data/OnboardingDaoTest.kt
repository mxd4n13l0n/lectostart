package com.lectostart.app.onboarding.data

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
class OnboardingDaoTest {
  private lateinit var db: LectoStartDatabase
  private lateinit var userDao: UserDao
  private lateinit var consentDao: ConsentDao

  @Before
  fun setup() {
    db =
      Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), LectoStartDatabase::class.java).allowMainThreadQueries().build()
    userDao = db.userDao()
    consentDao = db.consentDao()
  }

  @After
  fun tearDown() {
    db.close()
  }

  @Test
  fun insertAndReadUser() = runTest {
    val user = UserEntity(id = "u1", nickname = "Dani", age = 22, major = "Psicología", semester = "5", mainGoal = "comprender_mejor", createdAt = 1L)
    userDao.insert(user)

    assertEquals(user, userDao.getUser())
    assertEquals(user, userDao.observeUser().first())
  }

  @Test
  fun insertAndReadConsent() = runTest {
    val consent = ConsentEntity(userId = "u1", accepted = true, acceptedAt = 1L, textVersion = "v1")
    consentDao.insert(consent)

    assertEquals(consent, consentDao.getConsent("u1"))
  }
}
