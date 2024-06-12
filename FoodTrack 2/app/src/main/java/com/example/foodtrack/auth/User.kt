package com.example.foodtrack.auth

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val email: String,
    val password: String

) {
    override fun toString(): String {
        return "User(id=$id, email='$email', password='$password')"
    }
}
