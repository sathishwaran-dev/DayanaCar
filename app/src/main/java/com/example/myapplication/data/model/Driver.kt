package com.example.myapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drivers")
data class Driver(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val mobile: String,
    val email: String = "",
    val passwordHash: String,
    val licenceNumber: String,
    val licenceExpiry: String, // YYYY-MM-DD
    val address: String = "",
    val emergencyContact: String = "",
    val assignedCarId: Int? = null,
    val joiningDate: String = "",
    val baseSalary: Double = 20000.0,
    val status: String = "AVAILABLE", // AVAILABLE, ON_TRIP, OFFLINE, INACTIVE
    val notes: String = ""
)
