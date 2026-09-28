package com.example.myapplication.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

object Formatters {
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
        maximumFractionDigits = 2
    }

    fun formatCurrency(amount: Double): String {
        return try {
            currencyFormat.format(amount)
        } catch (e: Exception) {
            "₹${String.format(Locale.getDefault(), "%.2f", amount)}"
        }
    }

    fun formatKm(distance: Double): String {
        return String.format(Locale.getDefault(), "%.1f km", distance)
    }

    fun formatSpeed(speedKmh: Double): String {
        return String.format(Locale.getDefault(), "%.1f km/h", speedKmh)
    }

    fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun formatTimestamp(timestamp: Long): String {
        if (timestamp <= 0) return "-"
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun formatDuration(minutes: Long): String {
        val hours = minutes / 60
        val mins = minutes % 60
        return if (hours > 0) {
            "${hours}h ${mins}m"
        } else {
            "${mins}m"
        }
    }
}
