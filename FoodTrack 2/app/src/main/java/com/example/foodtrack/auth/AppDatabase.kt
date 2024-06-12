package com.example.foodtrack.auth
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ResultEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun resultDao(): ResultDao
}
