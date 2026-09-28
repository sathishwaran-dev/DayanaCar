package com.example.myapplication.data.dao

import androidx.room.*
import com.example.myapplication.data.model.Offer
import kotlinx.coroutines.flow.Flow

@Dao
interface OfferDao {
    @Query("SELECT * FROM offers ORDER BY minBookingsRequired ASC")
    fun getAllOffers(): Flow<List<Offer>>

    @Query("SELECT * FROM offers WHERE isActive = 1 AND minBookingsRequired <= :bookingCount")
    fun getOffersForCustomer(bookingCount: Int): Flow<List<Offer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOffer(offer: Offer): Long

    @Update
    suspend fun updateOffer(offer: Offer)

    @Delete
    suspend fun deleteOffer(offer: Offer)
}
