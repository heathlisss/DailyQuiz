package com.example.dailyquiz.data.remote

import com.squareup.moshi.Json

data class TriviaApiResponse(
    @Json(name = "response_code")
    val responseCode: Int,
    @Json(name = "results")
    val results: List<ApiQuestion>
)

data class ApiQuestion(
    @Json(name = "category")
    val category: String,
    @Json(name = "type")
    val type: String,
    @Json(name = "difficulty")
    val difficulty: String,
    @Json(name = "question")
    val questionText: String,
    @Json(name = "correct_answer")
    val correctAnswer: String,
    @Json(name = "incorrect_answers")
    val incorrectAnswers: List<String>
)