package com.example.appcontact.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

import com.example.appcontact.db.entity.Albums
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumsDao {
    @Query("SELECT * FROM albums ORDER BY artist ASC")
    fun getAll(): Flow<List<Albums>>

    @Insert
    suspend fun insertAll(album: Albums)

    @Update
    suspend fun update(album: Albums)

    @Delete
    suspend fun delete(album: Albums)
}