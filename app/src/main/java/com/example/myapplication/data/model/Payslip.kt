package com.example.myapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payslips")
data class Payslip(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val driverId: Int,
    val driverName: String,
    val payPeriod: String, // e.g. "February 2025"
    val basicSalary: Double,
    val allowances: Double = 0.0,
    val bonusAmount: Double = 0.0,
    val deductions: Double = 0.0,
    val netSalary: Double,
    val payDate: String, // YYYY-MM-DD
    val status: String = "GENERATED" // GENERATED, PAID
)
