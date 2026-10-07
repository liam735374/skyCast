package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Query("SELECT * FROM saved_locations ORDER BY displayOrder ASC, id ASC")
    fun getAllLocations(): Flow<List<SavedLocationEntity>>

    @Query("SELECT * FROM saved_locations WHERE id = :id LIMIT 1")
    suspend fun getLocationById(id: Long): SavedLocationEntity?

    @Query("SELECT * FROM saved_locations WHERE latitude = :lat AND longitude = :lon LIMIT 1")
    suspend fun findByCoordinates(lat: Double, lon: Double): SavedLocationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: SavedLocationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocations(locations: List<SavedLocationEntity>)

    @Update
    suspend fun updateLocation(location: SavedLocationEntity)

    @Delete
    suspend fun deleteLocation(location: SavedLocationEntity)

    @Query("DELETE FROM saved_locations WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE saved_locations SET cachedTempC = :tempC, cachedWeatherCode = :weatherCode, cachedCondition = :condition, updatedAt = :time WHERE id = :id")
    suspend fun updateWeatherCache(id: Long, tempC: Double, weatherCode: Int, condition: String, time: Long)

    @Query("SELECT COUNT(*) FROM saved_locations")
    suspend fun getCount(): Int
}
