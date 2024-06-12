package com.example.foodtrack.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromStringList(value: String?): List<String> {
        if (value.isNullOrEmpty()) {
            return emptyList()
        }
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun toStringList(list: List<String>?): String {
        return if (list.isNullOrEmpty()) {
            "[]"
        } else {
            val listType = object : TypeToken<List<String>>() {}.type
            Gson().toJson(list, listType)
        }
    }
}
