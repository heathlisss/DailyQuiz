package com.example.dailyquiz.data.repository

import android.text.Html
import com.example.dailyquiz.data.local.QuizAttemptEntity
import com.example.dailyquiz.data.local.QuizDao
import com.example.dailyquiz.data.model.Question
import com.example.dailyquiz.data.model.QuizHistoryItem
import com.example.dailyquiz.data.model.QuizReview
import com.example.dailyquiz.data.model.ReviewedQuestion
import com.example.dailyquiz.data.remote.OpenTdbApi

class QuizRepository(
    private val api: OpenTdbApi,
    private val dao: QuizDao
) {
    suspend fun getNewQuiz(category: Int, difficulty: String): Result<List<Question>> {
        return try {
            val response = api.getQuestions(category = category, difficulty = difficulty)
            if (response.isSuccessful && response.body() != null) {
                val domainQuestions = response.body()!!.results.map { apiQuestion ->
                    val allAnswers = (apiQuestion.incorrectAnswers + apiQuestion.correctAnswer)
                        .map { decodeHtml(it) }
                        .shuffled()

                    Question(
                        questionText = decodeHtml(apiQuestion.questionText),
                        correctAnswer = decodeHtml(apiQuestion.correctAnswer),
                        allShuffledAnswers = allAnswers,
                        category = decodeHtml(apiQuestion.category),
                        difficulty = apiQuestion.difficulty
                    )
                }
                Result.success(domainQuestions)
            } else {
                Result.failure(Exception("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getHistory(): List<QuizHistoryItem> {
        return dao.getAllAttempts().map { entity ->
            QuizHistoryItem(
                id = entity.id,
                timestamp = entity.timestamp,
                score = "${entity.correctAnswersCount}/${entity.totalQuestions}",
                category = entity.category,
                difficulty = entity.difficulty
            )
        }
    }

    suspend fun deleteHistoryItem(id: Long) {
        dao.deleteAttemptById(id)
    }

    suspend fun saveQuizResult(
        questions: List<Question>,
        userAnswers: Map<Int, String>
    ): Long {
        var correctAnswersCount = 0
        questions.forEachIndexed { index, question ->
            if (userAnswers[index] == question.correctAnswer) {
                correctAnswersCount++
            }
        }

        val attemptEntity = QuizAttemptEntity(
            timestamp = System.currentTimeMillis(),
            correctAnswersCount = correctAnswersCount,
            totalQuestions = questions.size,
            category = questions.firstOrNull()?.category ?: "Unknown",
            difficulty = questions.firstOrNull()?.difficulty ?: "Unknown"
        )

        return dao.saveFullQuizResult(
            attempt = attemptEntity,
            questions = questions,
            userAnswers = userAnswers
        )
    }

    suspend fun getQuizReview(attemptId: Long): QuizReview? {
        val attemptWithQuestions = dao.getAttemptWithQuestions(attemptId) ?: return null

        val reviewedQuestions = attemptWithQuestions.questions.map {
            ReviewedQuestion(
                questionText = it.questionText,
                allAnswers = it.allAnswers,
                correctAnswer = it.correctAnswer,
                userAnswer = it.userAnswer,
                wasCorrect = it.wasCorrect
            )
        }

        return QuizReview(
            questions = reviewedQuestions,
            category = attemptWithQuestions.attempt.category,
            difficulty = attemptWithQuestions.attempt.difficulty
        )
    }

    private fun decodeHtml(text: String): String {
        return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY).toString()
    }

    suspend fun getAttemptById(attemptId: Long): QuizAttemptEntity? {
        return dao.getAttemptById(attemptId)
    }
}