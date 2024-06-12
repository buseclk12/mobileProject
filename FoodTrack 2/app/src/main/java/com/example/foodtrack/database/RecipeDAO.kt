package com.example.foodtrack.database

import androidx.room.*

@Dao
interface RecipeDAO {
    @Insert
    fun insertData(recipe: RecipeModel)

    @Insert
    fun insertAllMock(recipes: List<RecipeModel>)

    @Query("SELECT * FROM ${Constants.RECIPE_TABLE}")
    fun getAllData(): List<RecipeModel>

    @Query("SELECT * FROM ${Constants.RECIPE_TABLE} WHERE id = :id")
    fun getRecipeById(id: Int): RecipeModel?

    @Query("SELECT * FROM ${Constants.RECIPE_TABLE} WHERE favorite = 1")
    fun getFavoriteRecipes(): List<RecipeModel>

    @Update
    fun updateData(recipe: RecipeModel)

    @Delete
    fun deleteData(recipe: RecipeModel)

    @Query("DELETE FROM ${Constants.RECIPE_TABLE}")
    fun deleteAll()
}
