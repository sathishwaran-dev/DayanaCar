package com.example.myapplication.data.dao

import androidx.room.*
import com.example.myapplication.data.model.Car
import kotlinx.coroutines.flow.Flow

@Dao
interface CarDao {
    @Query("SELECT * FROM cars ORDER BY brand ASC, model ASC")
    fun getAllCars(): Flow<List<Car>>

    @Query("SELECT * FROM cars WHERE status = 'AVAILABLE'")
    fun getAvailableCars(): Flow<List<Car>>

    @Query("SELECT * FROM cars WHERE id = :id")
    fun getCarById(id: Int): Flow<Car?>

    @Query("SELECT * FROM cars WHERE id = :id")
    suspend fun getCarByIdDirect(id: Int): Car?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCar(car: Car): Long

    @Update
    suspend fun updateCar(car: Car)

    @Delete
    suspend fun deleteCar(car: Car)

    @Query("UPDATE cars SET status = :status WHERE id = :id")
    suspend fun updateCarStatus(id: Int, status: String)

    @Query("UPDATE cars SET assignedDriverId = :driverId WHERE id = :carId")
    suspend fun assignDriverToCar(carId: Int, driverId: Int?)

    @Query("UPDATE cars SET currentOdometer = :newOdometer WHERE id = :carId")
    suspend fun updateOdometer(carId: Int, newOdometer: Double)
}
