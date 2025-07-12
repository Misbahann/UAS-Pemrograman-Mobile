package com.example.appcontact.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

import com.example.appcontact.db.entity.Songs
import kotlinx.coroutines.flow.Flow

@Dao
interface SongsDao {
    @Query("SELECT * FROM songs")
    fun getAll(): Flow<List<Songs>>

    @Insert
    fun insertAll(vararg songs: Songs)

    @Delete
    suspend fun delete(song: Songs)

    @Update
    suspend fun update(song: Songs)
}