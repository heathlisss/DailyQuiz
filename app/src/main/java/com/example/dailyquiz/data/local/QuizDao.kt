package com.example.dailyquiz.data.local

import androidx.room.*
import com.example.dailyquiz.data.local.model.QuestionResultEntity
import com.example.dailyquiz.data.local.model.QuizAttemptEntity

@Dao
interface QuizDao {

    @Transaction
    suspend fun insertAttemptWithQuestions(
        attempt: QuizAttemptEntity,
        questions: List<QuestionResultEntity>
    ) {
        val attemptId = insertAttempt(attempt)
        val questionsWithAttemptId = questions.map { it.copy(attemptId = attemptId) }
        insertAllQuestions(questionsWithAttemptId)
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