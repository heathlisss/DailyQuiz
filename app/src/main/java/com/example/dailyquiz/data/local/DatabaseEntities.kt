package com.example.dailyquiz.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "quiz_attempts")
data class QuizAttemptEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,
    val correctAnswersCount: Int,
    val totalQuestions: Int,
    val category: String,
    val difficulty: String
)

@Entity(tableName = "question_results")
@TypeConverters(StringListConverter::class)
data class QuestionResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val attemptId: Long,
    val questionText: String,
    val allAnswers: List<String>,
    val correctAnswer: String,
    val userAnswer: String,
    val wasCorrect: Boolean
)