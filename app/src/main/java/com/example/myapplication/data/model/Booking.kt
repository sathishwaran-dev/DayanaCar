package com.example.myapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class Booking(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val customerId: Int = 0,
    val customerName: String,
    val customerMobile: String,
    val carId: Int,
    val carNumber: String,
    val carModel: String = "",
    val driverId: Int? = null,
    val driverName: String = "",
    val pickupLocation: String,
    val destination: String,
    val bookingDate: String, // YYYY-MM-DD
    val bookingTime: String, // HH:mm
    val passengersCount: Int = 1,
    val fare: Double,
    val status: String = "PENDING", // PENDING, CONFIRMED, DRIVER_ASSIGNED, DRIVER_ACCEPTED, STARTED, COMPLETED, CANCELLED, REJECTED
    val notes: String = ""
)
