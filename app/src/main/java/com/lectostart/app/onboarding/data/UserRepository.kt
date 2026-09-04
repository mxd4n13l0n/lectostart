package com.lectostart.app.onboarding.data

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * Frontera de datos de onboarding (perfil + consentimiento). Ver docs/ARCHITECTURE.md §6: en
 * Fase 0/1 solo hay implementación Room (local-only); una futura implementación remota se
 * bindea aquí sin tocar los consumidores.
 *
 * Nota de flujo (docs/USER_STORIES.md US-02/US-03): el consentimiento se acepta antes de crear
 * el perfil, así que el `userId` (UUID) se genera en el caller (ViewModel de Consentimiento) y se
 * usa tanto para [recordConsent] como para [createUser].
 */
interface UserRepository {
  fun observeUser(): Flow<UserEntity?>

  suspend fun getUser(): UserEntity?

  suspend fun createUser(id: String, nickname: String, age: Int, major: String, semester: String, mainGoal: String): UserEntity

  suspend fun getConsent(userId: String): ConsentEntity?

  suspend fun recordConsent(userId: String, textVersion: String)
}

class UserRepositoryImpl @Inject constructor(private val userDao: UserDao, private val consentDao: ConsentDao) : UserRepository {
  override fun observeUser(): Flow<UserEntity?> = userDao.observeUser()

  override suspend fun getUser(): UserEntity? = userDao.getUser()

  override suspend fun createUser(id: String, nickname: String, age: Int, major: String, semester: String, mainGoal: String): UserEntity {
    val user =
      UserEntity(id = id, nickname = nickname, age = age, major = major, semester = semester, mainGoal = mainGoal, createdAt = System.currentTimeMillis())
    userDao.insert(user)
    return user
  }

  override suspend fun getConsent(userId: String): ConsentEntity? = consentDao.getConsent(userId)

  override suspend fun recordConsent(userId: String, textVersion: String) {
    consentDao.insert(ConsentEntity(userId = userId, accepted = true, acceptedAt = System.currentTimeMillis(), textVersion = textVersion))
  }
}
