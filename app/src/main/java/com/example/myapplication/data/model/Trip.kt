package com.example.myapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val carId: Int,
    val carNumber: String,
    val driverId: Int? = null,
    val driverName: String = "",
    val bookingId: Int? = null,
    val customerId: Int? = null,
    val customerName: String = "",
    val startLocation: String,
    val endLocation: String = "",
    val startLat: Double = 0.0,
    val startLng: Double = 0.0,
    val endLat: Double = 0.0,
    val endLng: Double = 0.0,
    val distanceKm: Double = 0.0,
    val durationMinutes: Long = 0,
    val currentSpeedKmh: Double = 0.0,
    val avgSpeedKmh: Double = 0.0,
    val maxSpeedKmh: Double = 0.0,
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long = 0,
    val isCompleted: Boolean = false,
    val dateString: String = ""
)
