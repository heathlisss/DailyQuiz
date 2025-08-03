package com.example.dailyquiz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyquiz.data.model.QuizHistoryItem
import com.example.dailyquiz.data.repository.QuizRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HistoryState(
    val attempts: List<QuizHistoryItem> = emptyList(),
    val isLoading: Boolean = true,
    val isEmpty: Boolean = false
)

sealed interface HistoryEvent {
    data class OnAttemptClicked(val id: Long) : HistoryEvent
    data object OnBackClicked : HistoryEvent
}

sealed interface HistoryNavigationEvent {
    data class ToReview(val attemptId: Long) : HistoryNavigationEvent
    data object GoBack : HistoryNavigationEvent
}

class HistoryViewModel(
    private val quizRepository: QuizRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryState())
    val state = _state.asStateFlow()

    private val _navigationEvent = Channel<HistoryNavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    init {
        loadHistory()
    }

    fun onEvent(event: HistoryEvent) {
        when (event) {
            is HistoryEvent.OnAttemptClicked -> {
                viewModelScope.launch {
                    _navigationEvent.send(HistoryNavigationEvent.ToReview(event.id))
                }
            }

            HistoryEvent.OnBackClicked -> {
                viewModelScope.launch {
                    _navigationEvent.send(HistoryNavigationEvent.GoBack)
                }
            }
        }
    }

    private fun loadHistory() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val historyItems = quizRepository.getHistory()
            _state.update {
                it.copy(
                    attempts = historyItems,
                    isLoading = false,
                    isEmpty = historyItems.isEmpty()
                )
            }
        }
    }
}