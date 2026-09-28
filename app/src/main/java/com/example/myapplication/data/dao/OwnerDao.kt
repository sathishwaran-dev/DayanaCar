package com.example.myapplication.data.dao

import androidx.room.*
import com.example.myapplication.data.model.Owner

@Dao
interface OwnerDao {
    @Query("SELECT * FROM owners WHERE username = :username LIMIT 1")
    suspend fun getOwnerByUsername(username: String): Owner?

    @Query("SELECT COUNT(*) FROM owners")
    suspend fun getOwnerCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOwner(owner: Owner): Long
}
