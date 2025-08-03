package com.example.dailyquiz.viewmodel // Убедитесь, что ваш пакет правильный

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyquiz.data.model.Question
import com.example.dailyquiz.data.repository.QuizRepository
import com.example.dailyquiz.ui.AppDestinations
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class QuizViewModel(
    private val quizRepository: QuizRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    sealed interface QuizState {
        data object Welcome : QuizState
        data object Loading : QuizState
        data class InProgress(
            val question: Question,
            val currentQuestionIndex: Int,
            val totalQuestions: Int,
            val selectedAnswer: String?,
            val isNextEnabled: Boolean,
            val isLastQuestion: Boolean
        ) : QuizState
        data class Error(val message: String) : QuizState
    }

    sealed interface QuizEvent {
        data object OnStartQuizClicked : QuizEvent
        data object OnHistoryClicked : QuizEvent
        data object OnNextQuestionClicked : QuizEvent
        data class OnAnswerSelected(val answer: String) : QuizEvent
        data object OnBackClicked : QuizEvent // ДОБАВЛЕНО
    }

    private val _navigationEvent = Channel<NavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    sealed interface NavigationEvent {
        data class ToResults(
            val attemptId: Long,
            val correctAnswers: Int,
            val totalQuestions: Int
        ) : NavigationEvent
        data object ToHistory : NavigationEvent
        data object ToWelcome : NavigationEvent // ДОБАВЛЕНО
    }

    private val _state = MutableStateFlow<QuizState>(QuizState.Welcome)
    val state = _state.asStateFlow()

    private var questions: List<Question> = emptyList()
    private var userAnswers: MutableMap<Int, String> = mutableMapOf()
    private var currentQuestionIndex = 0

    init {
        val attemptIdToRetry: Long = savedStateHandle[AppDestinations.ATTEMPT_ID_ARG] ?: -1L
        if (attemptIdToRetry != -1L) {
            retryQuiz(attemptIdToRetry)
        }
    }

    fun onEvent(event: QuizEvent) {
        when (event) {
            QuizEvent.OnStartQuizClicked -> startQuiz()
            QuizEvent.OnHistoryClicked -> viewModelScope.launch {
                _navigationEvent.send(NavigationEvent.ToHistory)
            }
            is QuizEvent.OnAnswerSelected -> selectAnswer(event.answer)
            QuizEvent.OnNextQuestionClicked -> loadNextQuestion()
            // ДОБАВЛЕНА ОБРАБОТКА
            QuizEvent.OnBackClicked -> {
                _state.value = QuizState.Welcome
                // Опционально, если хотите управлять через навигацию, а не просто сменой стейта
                // viewModelScope.launch { _navigationEvent.send(NavigationEvent.ToWelcome) }
            }
        }
    }

    private fun retryQuiz(attemptId: Long) {
        viewModelScope.launch {
            _state.value = QuizState.Loading
            val oldQuestions = quizRepository.getQuizForRetry(attemptId)
            if (!oldQuestions.isNullOrEmpty()) {
                questions = oldQuestions
                userAnswers.clear()
                currentQuestionIndex = 0
                loadQuestionAtIndex(0)
            } else {
                startQuiz()
            }
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
            val newSelectedAnswer = if (currentState.selectedAnswer == answer) null else answer
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
            isNextEnabled = false,
            isLastQuestion = (index == questions.size - 1)
        )
    }

    private fun finishQuiz() {
        Log.d("QuizVM", "Quiz Finished! Saving results... Answers: $userAnswers")
        viewModelScope.launch {
            try {
                val attemptId = quizRepository.saveQuizResult(questions, userAnswers)
                Log.d("QuizVM", "Results saved successfully with id $attemptId!")

                val correctCount = userAnswers.count { (index, answer) ->
                    questions.getOrNull(index)?.correctAnswer == answer
                }

                _navigationEvent.send(
                    NavigationEvent.ToResults(
                        attemptId,
                        correctCount,
                        questions.size
                    )
                )
            } catch (e: Exception) {
                Log.e("QuizVM", "Error saving results", e)
                _state.value = QuizState.Error("Ошибка при сохранении результатов.")
            }
        }
    }
}