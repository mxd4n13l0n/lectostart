package com.lectostart.app.onboarding.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ConsentEntity(@PrimaryKey val userId: String, val accepted: Boolean, val acceptedAt: Long, val textVersion: String)
