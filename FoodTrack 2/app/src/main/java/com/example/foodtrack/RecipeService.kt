package com.example.foodtrack

import com.example.foodtrack.database.RecipeModel
import retrofit2.Call
import retrofit2.http.GET

interface RecipeService {
    @GET("recipes.json")
    fun getRecipes(): Call<List<RecipeModel>>
}
