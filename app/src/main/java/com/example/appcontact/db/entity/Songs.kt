package com.example.appcontact.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Songs(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "songs") val songsName: String?,
    @ColumnInfo(name = "musisi") val musisiuName: String?
)