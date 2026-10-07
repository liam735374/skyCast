package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_locations")
data class SavedLocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val country: String,
    val admin1: String? = null,
    val latitude: Double,
    val longitude: Double,
    val isPinned: Boolean = true,
    val displayOrder: Int = 0,
    val cachedTempC: Double? = null,
    val cachedWeatherCode: Int? = null,
    val cachedCondition: String? = null,
    val updatedAt: Long = System.currentTimeMillis()
)
