package com.example.myapplication.data.dao

import androidx.room.*
import com.example.myapplication.data.model.DriverBonus
import kotlinx.coroutines.flow.Flow

@Dao
interface DriverBonusDao {
    @Query("SELECT * FROM driver_bonuses ORDER BY date DESC")
    fun getAllBonuses(): Flow<List<DriverBonus>>

    @Query("SELECT * FROM driver_bonuses WHERE driverId = :driverId ORDER BY date DESC")
    fun getBonusesByDriverId(driverId: Int): Flow<List<DriverBonus>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBonus(bonus: DriverBonus): Long

    @Update
    suspend fun updateBonus(bonus: DriverBonus)

    @Query("UPDATE driver_bonuses SET status = :status WHERE id = :id")
    suspend fun updateBonusStatus(id: Int, status: String)
}
