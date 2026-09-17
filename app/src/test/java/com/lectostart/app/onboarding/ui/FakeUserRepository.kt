package com.lectostart.app.onboarding.ui

import com.lectostart.app.onboarding.data.ConsentEntity
import com.lectostart.app.onboarding.data.UserEntity
import com.lectostart.app.onboarding.data.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/** Test double compartido por ConsentViewModelTest y CreateProfileViewModelTest. */
class FakeUserRepository : UserRepository {
  val consentsRecorded = mutableMapOf<String, String>()
  var createdUser: UserEntity? = null

  override fun observeUser(): Flow<UserEntity?> = MutableStateFlow(createdUser)

  override suspend fun getUser(): UserEntity? = createdUser

  override suspend fun createUser(id: String, nickname: String, age: Int, major: String, semester: String, mainGoal: String): UserEntity {
    val user = UserEntity(id, nickname, age, major, semester, mainGoal, createdAt = 0L)
    createdUser = user
    return user
  }

  override suspend fun getConsent(userId: String): ConsentEntity? = null

  override suspend fun recordConsent(userId: String, textVersion: String) {
    consentsRecorded[userId] = textVersion
  }

  override suspend fun updateLastCelebratedStreakMilestone(userId: String, milestone: Int) {
    createdUser = createdUser?.copy(lastCelebratedStreakMilestone = milestone)
  }
}
