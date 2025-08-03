package com.example.dailyquiz.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyquiz.data.model.Question
import com.example.dailyquiz.data.repository.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QuizViewModel(private val quizRepository: QuizRepository) : ViewModel() {

    sealed interface QuizState {
        data object Welcome : QuizState
        data object Loading : QuizState
        data class InProgress(
            val question: Question,
            val currentQuestionIndex: Int,
            val totalQuestions: Int,
            val selectedAnswer: String?,
            val isNextEnabled: Boolean
        ) : QuizState

        data class Error(val message: String) : QuizState
    }

    sealed interface QuizEvent {
        data object OnStartQuizClicked : QuizEvent
        data object OnHistoryClicked : QuizEvent
        data object OnNextQuestionClicked : QuizEvent
        data class OnAnswerSelected(val answer: String) : QuizEvent
    }

    private val _state = MutableStateFlow<QuizState>(QuizState.Welcome)
    val state = _state.asStateFlow()

    private var questions: List<Question> = emptyList()
    private var userAnswers: MutableMap<Int, String> = mutableMapOf()
    private var currentQuestionIndex = 0

    fun onEvent(event: QuizEvent) {
        when (event) {
            QuizEvent.OnStartQuizClicked -> startQuiz()
            QuizEvent.OnHistoryClicked -> Log.d("QuizVM", "Navigate to History")
            is QuizEvent.OnAnswerSelected -> selectAnswer(event.answer)
            QuizEvent.OnNextQuestionClicked -> loadNextQuestion()
        }
    }

    private fun startQuiz() {
        viewModelScope.launch {
            _state.value = QuizState.Loading
            quizRepository.getNewQuiz(category = 9, difficulty = "easy")
                .onSuccess { loadedQuestions ->
                    questions = loadedQuestions
                    userAnswers.clear()
                    currentQuestionIndex = 0
                    if (questions.isNotEmpty()) {
                        loadQuestionAtIndex(0)
                    } else {
                        _state.value = QuizState.Error("Не удалось загрузить вопросы.")
                    }
                }
                .onFailure { error ->
                    _state.value = QuizState.Error(error.message ?: "Произошла ошибка")
                    Log.e("QuizVM", "Error loading quiz", error)
                }
        }
    }

    private fun selectAnswer(answer: String) {
        val currentState = _state.value
        if (currentState is QuizState.InProgress) {
            val newSelectedAnswer = if (currentState.selectedAnswer == answer) {
                null // Если кликнули по уже выбранному - отменяем выбор
            } else {
                answer // Выбираем новый
            }

            _state.value = currentState.copy(
                selectedAnswer = newSelectedAnswer,
                isNextEnabled = newSelectedAnswer != null
            )
        }
    }

    private fun loadNextQuestion() {
        val currentState = _state.value
        if (currentState is QuizState.InProgress && currentState.selectedAnswer != null) {
            userAnswers[currentQuestionIndex] = currentState.selectedAnswer
        }

        currentQuestionIndex++
        if (currentQuestionIndex < questions.size) {
            loadQuestionAtIndex(currentQuestionIndex)
        } else {
            finishQuiz()
        }
    }

    private fun loadQuestionAtIndex(index: Int) {
        _state.value = QuizState.InProgress(
            question = questions[index],
            currentQuestionIndex = index,
            totalQuestions = questions.size,
            selectedAnswer = null,
            isNextEnabled = false
        )
    }

    private fun finishQuiz() {
        Log.d("QuizVM", "Quiz Finished! Answers: $userAnswers")
        _state.value = QuizState.Welcome
    }
}