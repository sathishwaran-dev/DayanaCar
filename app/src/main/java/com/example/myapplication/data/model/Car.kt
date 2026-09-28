package com.example.myapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cars")
data class Car(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val carNumber: String,
    val brand: String,
    val model: String,
    val year: Int,
    val carImage: String = "",
    val fuelType: String, // Petrol, Diesel, Electric, Hybrid, CNG
    val seatingCapacity: Int = 5,
    val currentOdometer: Double,
    val insuranceExpiry: String, // YYYY-MM-DD
    val serviceDueDate: String, // YYYY-MM-DD
    val status: String = "AVAILABLE", // AVAILABLE, BOOKED, ON_TRIP, MAINTENANCE, INACTIVE
    val assignedDriverId: Int? = null,
    val farePerKm: Double = 15.0,
    val notes: String = ""
)
