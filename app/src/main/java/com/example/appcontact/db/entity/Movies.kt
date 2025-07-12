package com.example.appcontact.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class Movies(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "movies") val moviesName: String?,
    @ColumnInfo(name = "director") val directorName: String?
)