package com.example.myapplication.data.dao

import androidx.room.*
import com.example.myapplication.data.model.Expense
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY timestamp DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE carId = :carId ORDER BY timestamp DESC")
    fun getExpensesByCarId(carId: Int): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE date = :date ORDER BY timestamp DESC")
    fun getExpensesByDate(date: String): Flow<List<Expense>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense): Long

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)
}
