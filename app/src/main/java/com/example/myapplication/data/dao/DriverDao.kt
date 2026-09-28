package com.example.myapplication.data.dao

import androidx.room.*
import com.example.myapplication.data.model.Driver
import kotlinx.coroutines.flow.Flow

@Dao
interface DriverDao {
    @Query("SELECT * FROM drivers ORDER BY name ASC")
    fun getAllDrivers(): Flow<List<Driver>>

    @Query("SELECT * FROM drivers WHERE id = :id")
    fun getDriverById(id: Int): Flow<Driver?>

    @Query("SELECT * FROM drivers WHERE mobile = :mobile LIMIT 1")
    suspend fun getDriverByMobile(mobile: String): Driver?

    @Query("SELECT * FROM drivers WHERE status = 'AVAILABLE'")
    fun getAvailableDrivers(): Flow<List<Driver>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDriver(driver: Driver): Long

    @Update
    suspend fun updateDriver(driver: Driver)

    @Delete
    suspend fun deleteDriver(driver: Driver)

    @Query("UPDATE drivers SET status = :status WHERE id = :id")
    suspend fun updateDriverStatus(id: Int, status: String)

    @Query("UPDATE drivers SET assignedCarId = :carId WHERE id = :driverId")
    suspend fun assignCarToDriver(driverId: Int, carId: Int?)
}
