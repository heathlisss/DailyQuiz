package com.example.dailyquiz.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.dailyquiz.data.local.QuizDao
import com.example.dailyquiz.data.local.model.QuestionResultEntity
import com.example.dailyquiz.data.local.model.QuizAttemptEntity
import com.example.dailyquiz.data.local.StringListConverter

@Database(
    entities = [QuizAttemptEntity::class, QuestionResultEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(StringListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun quizDao(): QuizDao
}