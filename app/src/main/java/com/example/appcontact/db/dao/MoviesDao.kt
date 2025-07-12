package com.example.appcontact.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.appcontact.db.entity.Movies
import kotlinx.coroutines.flow.Flow

@Dao
interface MoviesDao {
    @Query("SELECT * FROM movies")
    fun getAll(): Flow<List<Movies>>

    @Insert
    fun insertAll(vararg movie: Movies)

    @Delete
    suspend fun delete(movie: Movies)

    @Update
    suspend fun update(movie: Movies)
}