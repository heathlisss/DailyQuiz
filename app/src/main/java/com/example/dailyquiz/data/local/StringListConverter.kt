package com.example.dailyquiz.data.local

import androidx.room.TypeConverter

class StringListConverter {
    private val separator = "|||---|||"

    @TypeConverter
    fun fromStringList(list: List<String>): String {
        return list.joinToString(separator)
    }

    @TypeConverter
    fun toStringList(data: String): List<String> {
        return if (data.isBlank()) emptyList() else data.split(separator)
    }
}