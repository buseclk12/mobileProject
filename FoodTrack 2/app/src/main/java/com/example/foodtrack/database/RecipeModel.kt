package com.example.foodtrack.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.foodtrack.database.Constants.RECIPE_TABLE
import java.io.Serializable

@Entity(tableName = RECIPE_TABLE)
@TypeConverters(Converters::class)
data class RecipeModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    var ingredients: List<String> = emptyList(),
    var instructions: List<String> = emptyList(),
    val image: String,
    var favorite: Boolean,
    val category: String
) : Serializable
