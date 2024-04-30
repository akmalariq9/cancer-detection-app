package com.dicoding.asclepius.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "History")
data class History(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val imageURL: String,
    val result: String,
)

