package com.example.dailyquiz.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyquiz.data.model.QuizReview
import com.example.dailyquiz.data.repository.QuizRepository
import com.example.dailyquiz.ui.AppDestinations
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ResultsViewModel(
    savedStateHandle: SavedStateHandle,
    private val quizRepository: QuizRepository
) : ViewModel() {

    private data class ResultText(val title: String, val subtitle: String)

    data class ResultsState(
        val correctAnswersCount: Int = 0,
        val totalQuestions: Int = 0,
        val resultTitle: String = "",
        val resultSubtitle: String = "",
        val isReviewVisible: Boolean = false,
        val quizReview: QuizReview? = null
    )

    private val _state = MutableStateFlow(ResultsState())
    val state = _state.asStateFlow()

    // Теперь AppDestinations.ATTEMPT_ID_ARG будет виден
    private val attemptId: Long = savedStateHandle[AppDestinations.ATTEMPT_ID_ARG] ?: -1L

    init {
        val correctAnswers: Int = savedStateHandle[AppDestinations.CORRECT_ANSWERS_ARG] ?: 0
        val totalQuestions: Int = savedStateHandle[AppDestinations.TOTAL_QUESTIONS_ARG] ?: 5
        val resultText = getResultTexts(correctAnswers)

        _state.value = ResultsState(
            correctAnswersCount = correctAnswers,
            totalQuestions = totalQuestions,
            resultTitle = resultText.title,
            resultSubtitle = resultText.subtitle
        )
    }

    fun onToggleReview() {
        val isCurrentlyVisible = _state.value.isReviewVisible
        _state.update { it.copy(isReviewVisible = !isCurrentlyVisible) }

        if (!isCurrentlyVisible && _state.value.quizReview == null) {
            loadQuizReview()
        }
    }

    private fun loadQuizReview() {
        if (attemptId == -1L) return
        viewModelScope.launch {
            val reviewData = quizRepository.getQuizReview(attemptId)
            _state.update { it.copy(quizReview = reviewData) }
        }
    }

    private fun getResultTexts(correctCount: Int): ResultText {
        return when (correctCount) {
            5 -> ResultText(
                "Идеально!",
                "5/5 — вы ответили на всё правильно. Это блестящий результат!"
            )
            4 -> ResultText("Почти идеально!", "4/5 — очень близко к совершенству. Ещё один шаг!")
            3 -> ResultText(
                "Хороший результат!",
                "3/5 — вы на верном пути. Продолжайте тренироваться!"
            )

            2 -> ResultText(
                "Есть над чем поработать",
                "2/5 — не расстраивайтесь, попробуйте ещё раз!"
            )

            1 -> ResultText(
                "Сложный вопрос?",
                "1/5 — иногда просто не ваш день. Следующая попытка будет лучше!"
            )

            else -> ResultText(
                "Бывает и так!",
                "0/5 — не отчаивайтесь. Начните заново и удивите себя!"
            )
        }
    }
}