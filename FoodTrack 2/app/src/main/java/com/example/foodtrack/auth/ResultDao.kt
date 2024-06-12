package com.example.foodtrack.auth


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ResultDao {
    @Insert
    fun insert(result: ResultEntity)

    @Query("SELECT * FROM resultentity")
    fun getAll(): List<ResultEntity>
}
