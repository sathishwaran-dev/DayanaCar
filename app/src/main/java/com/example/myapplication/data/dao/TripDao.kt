package com.example.myapplication.data.dao

import androidx.room.*
import com.example.myapplication.data.model.Trip
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Query("SELECT * FROM trips ORDER BY startTime DESC")
    fun getAllTrips(): Flow<List<Trip>>

    @Query("SELECT * FROM trips WHERE id = :id")
    fun getTripById(id: Int): Flow<Trip?>

    @Query("SELECT * FROM trips WHERE isCompleted = 0 LIMIT 1")
    fun getActiveTrip(): Flow<Trip?>

    @Query("SELECT * FROM trips WHERE isCompleted = 0 LIMIT 1")
    suspend fun getActiveTripDirect(): Trip?

    @Query("SELECT * FROM trips WHERE carId = :carId ORDER BY startTime DESC")
    fun getTripsByCarId(carId: Int): Flow<List<Trip>>

    @Query("SELECT * FROM trips WHERE dateString = :dateString ORDER BY startTime DESC")
    fun getTripsByDate(dateString: String): Flow<List<Trip>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: Trip): Long

    @Update
    suspend fun updateTrip(trip: Trip)

    @Delete
    suspend fun deleteTrip(trip: Trip)
}
