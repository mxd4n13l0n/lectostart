package com.lectostart.app.core.data

import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomDatabase

/**
 * Base de datos local (Room) de LectoStart. Local-only para el MVP (ver docs/MVP.md §6).
 *
 * [PlaceholderEntity] existe solo porque Room exige al menos una entidad declarada; se elimina en
 * T-007 (docs/BACKLOG.md) cuando se agregan las entidades reales (UserEntity, SessionEntity, etc.
 * de docs/ARCHITECTURE.md §5).
 */
@Database(entities = [PlaceholderEntity::class], version = 1, exportSchema = false)
abstract class LectoStartDatabase : RoomDatabase()

@Entity internal data class PlaceholderEntity(@PrimaryKey val id: Int = 0)
