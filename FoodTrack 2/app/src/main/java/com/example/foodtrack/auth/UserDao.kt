package com.example.foodtrack.auth

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User)

    @Update
    fun updateUser(user: User)

    @Delete
    fun deleteUser(user: User)

    @Query("DELETE FROM users")
    fun deleteAllUsers()

    @Query("SELECT * FROM users ORDER BY id DESC")
    fun getAllUsers(): List<User>

    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserById(id: Int): User?

    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    fun getUser(email: String, password: String): User?

    @Query("SELECT * FROM users WHERE email LIKE :email")
    fun getUsersByEmail(email: String): List<User>

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    fun getUserByEmail(email: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllUsers(users: List<User>) {
        users.forEach {
            insertUser(it)
        }
    }
}
