package com.example.appcontact.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.appcontact.db.dao.AlbumsDao
import com.example.appcontact.db.dao.MoviesDao
import com.example.appcontact.db.dao.SongsDao
import com.example.appcontact.db.entity.Albums
import com.example.appcontact.db.entity.Movies
import com.example.appcontact.db.entity.Songs

@Database(entities = [Songs::class, Albums::class, Movies::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun songDao(): SongsDao
    abstract fun albumDao(): AlbumsDao
    abstract fun movieDao(): MoviesDao
    // Companion object untuk membuat instance database (Singleton)
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            // synchronized memastikan hanya satu thread yang bisa mengakses blok ini pada satu waktu
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "My Favorite" // Nama file database Anda
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}