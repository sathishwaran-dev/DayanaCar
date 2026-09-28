package com.example.myapplication.data.dao

import androidx.room.*
import com.example.myapplication.data.model.Booking
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {
    @Query("SELECT * FROM bookings ORDER BY id DESC")
    fun getAllBookings(): Flow<List<Booking>>

    @Query("SELECT * FROM bookings WHERE id = :id")
    fun getBookingById(id: Int): Flow<Booking?>

    @Query("SELECT * FROM bookings WHERE customerId = :customerId ORDER BY id DESC")
    fun getBookingsByCustomerId(customerId: Int): Flow<List<Booking>>

    @Query("SELECT * FROM bookings WHERE driverId = :driverId ORDER BY id DESC")
    fun getBookingsByDriverId(driverId: Int): Flow<List<Booking>>

    @Query("SELECT * FROM bookings WHERE customerMobile = :mobile ORDER BY id DESC")
    fun getBookingsByCustomerMobile(mobile: String): Flow<List<Booking>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: Booking): Long

    @Update
    suspend fun updateBooking(booking: Booking)

    @Delete
    suspend fun deleteBooking(booking: Booking)

    @Query("UPDATE bookings SET status = :status WHERE id = :id")
    suspend fun updateBookingStatus(id: Int, status: String)

    @Query("UPDATE bookings SET driverId = :driverId, driverName = :driverName, status = 'DRIVER_ASSIGNED' WHERE id = :bookingId")
    suspend fun assignDriverToBooking(bookingId: Int, driverId: Int, driverName: String)
}
