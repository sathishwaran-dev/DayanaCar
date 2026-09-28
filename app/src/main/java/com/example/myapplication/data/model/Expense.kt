package com.example.myapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val carId: Int,
    val carNumber: String,
    val driverId: Int? = null,
    val driverName: String = "",
    val amount: Double,
    val date: String, // YYYY-MM-DD
    val timestamp: Long = System.currentTimeMillis(),
    val category: String, // Fuel, Service, Repair, Insurance, Toll, Parking, Other
    val description: String = "",
    val odometerReading: Double = 0.0
)
