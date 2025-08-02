package com.example.dailyquiz.data.model

data class Question(
    val questionText: String,
    val correctAnswer: String,
    val allShuffledAnswers: List<String>,
    val category: String,
    val difficulty: String
)

data class QuizHistoryItem(
    val id: Long,
    val timestamp: Long,
    val score: String,
    val category: String,
    val difficulty: String
)

data class QuizReview(
    val questions: List<ReviewedQuestion>,
    val category: String,
    val difficulty: String
)

data class ReviewedQuestion(
    val questionText: String,
    val allAnswers: List<String>,
    val correctAnswer: String,
    val userAnswer: String,
    val wasCorrect: Boolean
)