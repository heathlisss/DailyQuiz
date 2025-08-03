package com.example.dailyquiz.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.dailyquiz.data.model.Question

@Dao
interface QuizDao {

    @Transaction
    suspend fun saveFullQuizResult(
        attempt: QuizAttemptEntity,
        questions: List<Question>,
        userAnswers: Map<Int, String>
    ) {
        val attemptId = insertAttempt(attempt)

        val questionResultEntities = questions.mapIndexed { index, question ->
            val userAnswer = userAnswers[index] ?: ""
            QuestionResultEntity(
                attemptId = attemptId,
                questionText = question.questionText,
                allAnswers = question.allShuffledAnswers,
                correctAnswer = question.correctAnswer,
                userAnswer = userAnswer,
                wasCorrect = userAnswer == question.correctAnswer
            )
        }

        insertAllQuestions(questionResultEntities)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: QuizAttemptEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllQuestions(questions: List<QuestionResultEntity>)

    @Query("SELECT * FROM quiz_attempts ORDER BY timestamp DESC")
    suspend fun getAllAttempts(): List<QuizAttemptEntity>

    @Query("SELECT * FROM question_results WHERE attemptId = :attemptId")
    suspend fun getQuestionsForAttempt(attemptId: Long): List<QuestionResultEntity>

    @Query("DELETE FROM quiz_attempts WHERE id = :attemptId")
    suspend fun deleteAttemptById(attemptId: Long)
}