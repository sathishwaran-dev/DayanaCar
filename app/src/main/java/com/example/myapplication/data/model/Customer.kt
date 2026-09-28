package com.example.myapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val mobile: String,
    val email: String = "",
    val passwordHash: String = "123456",
    val pickupLocation: String = "",
    val destination: String = "",
    val status: String = "ACTIVE", // ACTIVE, INACTIVE
    val notes: String = ""
)
