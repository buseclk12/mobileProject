package com.example.foodtrack.auth

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val result: String
)

