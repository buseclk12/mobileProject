package com.example.foodtrack.util

import android.content.Context
import java.io.IOException
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.foodtrack.database.RecipeModel


object JsonHelper {
    fun parseRecipes(json: String): List<RecipeModel> {
        val listType = object : TypeToken<List<RecipeModel>>() {}.type
        return Gson().fromJson(json, listType)

    }
    fun loadJSONFromAsset(context: Context, fileName: String): String? {
        return try {
            val inputStream = context.assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            null
        }

    }
}


