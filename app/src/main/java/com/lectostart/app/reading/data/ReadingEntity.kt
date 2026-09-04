package com.lectostart.app.reading.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity data class ReadingEntity(@PrimaryKey val id: String, val title: String, val text: String, val source: String?, val createdAt: Long)
