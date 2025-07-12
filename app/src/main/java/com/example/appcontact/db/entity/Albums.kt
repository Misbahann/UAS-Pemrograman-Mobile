package com.example.appcontact.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Albums(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "album") val albumName: String?,
    @ColumnInfo(name = "artist") val artistName: String?,
    @ColumnInfo(name="tahun") val tahunAlbum: String?
)
