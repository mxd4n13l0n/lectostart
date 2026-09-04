package com.lectostart.app.reading.data

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * Frontera de datos de lecturas y sus preguntas. `insertReadings`/`insertQuestions` existen para
 * que el sembrado de contenido fijo (T-009, docs/BACKLOG.md) pase por el repositorio en vez de
 * tocar los DAOs directamente.
 */
interface ReadingRepository {
  fun observeReadings(): Flow<List<ReadingEntity>>

  suspend fun getReading(id: String): ReadingEntity?

  suspend fun insertReadings(readings: List<ReadingEntity>)

  fun observeQuestions(readingId: String): Flow<List<QuestionEntity>>

  suspend fun getQuestions(readingId: String): List<QuestionEntity>

  suspend fun insertQuestions(questions: List<QuestionEntity>)
}

class ReadingRepositoryImpl @Inject constructor(private val readingDao: ReadingDao, private val questionDao: QuestionDao) : ReadingRepository {
  override fun observeReadings(): Flow<List<ReadingEntity>> = readingDao.observeAll()

  override suspend fun getReading(id: String): ReadingEntity? = readingDao.getById(id)

  override suspend fun insertReadings(readings: List<ReadingEntity>) = readingDao.insertAll(readings)

  override fun observeQuestions(readingId: String): Flow<List<QuestionEntity>> = questionDao.observeForReading(readingId)

  override suspend fun getQuestions(readingId: String): List<QuestionEntity> = questionDao.getForReading(readingId)

  override suspend fun insertQuestions(questions: List<QuestionEntity>) = questionDao.insertAll(questions)
}
