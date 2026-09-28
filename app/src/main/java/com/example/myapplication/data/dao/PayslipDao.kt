package com.example.myapplication.data.dao

import androidx.room.*
import com.example.myapplication.data.model.Payslip
import kotlinx.coroutines.flow.Flow

@Dao
interface PayslipDao {
    @Query("SELECT * FROM payslips ORDER BY payDate DESC")
    fun getAllPayslips(): Flow<List<Payslip>>

    @Query("SELECT * FROM payslips WHERE driverId = :driverId ORDER BY payDate DESC")
    fun getPayslipsByDriverId(driverId: Int): Flow<List<Payslip>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayslip(payslip: Payslip): Long

    @Update
    suspend fun updatePayslip(payslip: Payslip)
}
