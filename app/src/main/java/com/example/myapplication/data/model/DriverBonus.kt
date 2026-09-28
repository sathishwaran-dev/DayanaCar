package com.example.myapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "driver_bonuses")
data class DriverBonus(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val driverId: Int,
    val driverName: String,
    val bonusAmount: Double,
    val reason: String,
    val tripsTarget: Int = 0,
    val distanceTargetKm: Double = 0.0,
    val month: String, // YYYY-MM
    val status: String = "PENDING", // PENDING, APPROVED, PAID
    val date: String = ""
)
