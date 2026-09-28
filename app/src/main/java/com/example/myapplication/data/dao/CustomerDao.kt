package com.example.myapplication.data.dao

import androidx.room.*
import com.example.myapplication.data.model.Customer
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers ORDER BY name ASC")
    fun getAllCustomers(): Flow<List<Customer>>

    @Query("SELECT * FROM customers WHERE id = :id")
    fun getCustomerById(id: Int): Flow<Customer?>

    @Query("SELECT * FROM customers WHERE id = :id")
    suspend fun getCustomerByIdDirect(id: Int): Customer?

    @Query("SELECT * FROM customers WHERE mobile = :mobile LIMIT 1")
    suspend fun getCustomerByMobile(mobile: String): Customer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: Customer): Long

    @Update
    suspend fun updateCustomer(customer: Customer)

    @Delete
    suspend fun deleteCustomer(customer: Customer)

    @Query("UPDATE customers SET status = :status WHERE id = :id")
    suspend fun updateCustomerStatus(id: Int, status: String)
}
