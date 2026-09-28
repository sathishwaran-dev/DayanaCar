package com.example.myapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "offers")
data class Offer(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val offerName: String,
    val description: String,
    val discountPercentage: Double = 10.0,
    val minBookingsRequired: Int = 5,
    val startDate: String = "",
    val endDate: String = "",
    val isActive: Boolean = true
)
