package com.dicoding.asclepius.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HistoryDao {
    @Query("SELECT * FROM History")
    suspend fun getAllData(): List<History>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertData(history: History)

    @Delete
    suspend fun deleteData(history: History)
}

