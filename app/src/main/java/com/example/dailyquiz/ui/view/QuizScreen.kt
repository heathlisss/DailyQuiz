package com.example.dailyquiz.ui.view

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dailyquiz.R
import com.example.dailyquiz.data.model.Question
import com.example.dailyquiz.ui.theme.DailyQuizTheme
import com.example.dailyquiz.ui.theme.Gray
import com.example.dailyquiz.ui.theme.LightPurple
import com.example.dailyquiz.ui.theme.Purple
import com.example.dailyquiz.viewmodel.QuizViewModel
import com.example.dailyquiz.viewmodel.QuizViewModel.QuizEvent
import com.example.dailyquiz.viewmodel.QuizViewModel.QuizState
import org.koin.androidx.compose.koinViewModel

@Composable
fun QuizScreen(
    onQuizFinished: (correct: Int, total: Int) -> Unit,
    onHistoryClicked: () -> Unit
) {
    val viewModel: QuizViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is QuizViewModel.NavigationEvent.ToResults -> onQuizFinished(
                    event.correctAnswers,
                    event.totalQuestions
                )

                QuizViewModel.NavigationEvent.ToHistory -> onHistoryClicked()
            }
        }
    }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when (val currentState = state) {
                is QuizState.Welcome -> WelcomeContent(
                    onStartClick = { viewModel.onEvent(QuizEvent.OnStartQuizClicked) },
                    onHistoryClick = { viewModel.onEvent(QuizEvent.OnHistoryClicked) }
                )
                is QuizState.Loading -> LoadingContent()
                is QuizState.InProgress -> InProgressContent(
                    state = currentState,
                    onAnswerSelected = { answer ->
                        viewModel.onEvent(QuizEvent.OnAnswerSelected(answer))
                    },
                    onNextClick = { viewModel.onEvent(QuizEvent.OnNextQuestionClicked) }
                )
                is QuizState.Error -> {
                    Text(text = currentState.message, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun WelcomeContent(
    onStartClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onHistoryClick,
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .align(Alignment.CenterHorizontally),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = Purple
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "История",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    painter = painterResource(R.drawable.history_icon),
                    contentDescription = null,
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "Логотип DailyQuiz",
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(modifier = Modifier.height(32.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp),
            shape = RoundedCornerShape(48.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Добро пожаловать в DailyQuiz!",
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = onStartClick,
                    modifier = Modifier.fillMaxWidth(0.9f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Purple,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = "Начать викторину",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun LoadingContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "Логотип DailyQuiz",
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(modifier = Modifier.height(32.dp))
        val infiniteTransition = rememberInfiniteTransition(label = "rotation_transition")
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotation_animation"
        )
        Image(
            painter = painterResource(R.drawable.resource_default),
            contentDescription = "Загрузка вопросов",
            modifier = Modifier.rotate(rotation)
        )
        Spacer(modifier = Modifier.height(64.dp))
    }
}

@Composable
private fun InProgressContent(
    state: QuizState.InProgress,
    onAnswerSelected: (String) -> Unit,
    onNextClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "Логотип DailyQuiz",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(200.dp),
        )
        Spacer(modifier = Modifier.height(32.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(48.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Вопрос ${state.currentQuestionIndex + 1} из ${state.totalQuestions}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = LightPurple,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = state.question.questionText,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                state.question.allShuffledAnswers.forEach { answer ->
                    val answerState = if (answer == state.selectedAnswer) {
                        AnswerState.SELECTED
                    } else {
                        AnswerState.DEFAULT
                    }
                    AnswerOption(
                        text = answer,
                        state = answerState,
                        onClick = { onAnswerSelected(answer) },
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onNextClick,
                    enabled = state.isNextEnabled,
                    modifier = Modifier.fillMaxWidth(0.7f),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = Gray,
                        disabledContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = if (state.isLastQuestion) "Завершить" else "Далее",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true, name = "1. Welcome Content")
@Composable
private fun WelcomeContentPreview() {
    DailyQuizTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            WelcomeContent(onStartClick = {}, onHistoryClick = {})
        }
    }
}

@Preview(showBackground = true, name = "2. Loading State Content")
@Composable
private fun LoadingStatePreview() {
    DailyQuizTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            LoadingContent()
        }
    }
}

@Preview(showBackground = true, name = "3. In Progress - Not Selected Content")
@Composable
private fun InProgressContent_NotSelectedPreview() {
    val fakeQuestion = Question(
        questionText = "Какой фреймворк используется для декларативного UI в Android?",
        correctAnswer = "Jetpack Compose",
        allShuffledAnswers = listOf("XML Layouts", "Jetpack Compose", "Flutter", "SwiftUI"),
        category = "Разработка",
        difficulty = "Easy"
    )
    val fakeState = QuizState.InProgress(
        question = fakeQuestion,
        currentQuestionIndex = 2,
        totalQuestions = 5,
        selectedAnswer = null,
        isNextEnabled = false,
        isLastQuestion = false
    )

    DailyQuizTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            InProgressContent(
                state = fakeState,
                onAnswerSelected = {},
                onNextClick = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "4. In Progress - Answer Selected Content")
@Composable
private fun InProgressContent_AnswerSelectedPreview() {
    val fakeQuestion = Question(
        questionText = "Какой фреймворк используется для декларативного UI в Android?",
        correctAnswer = "Jetpack Compose",
        allShuffledAnswers = listOf("XML Layouts", "Jetpack Compose", "Flutter", "SwiftUI"),
        category = "Разработка",
        difficulty = "Easy"
    )
    val fakeState = QuizState.InProgress(
        question = fakeQuestion,
        currentQuestionIndex = 2,
        totalQuestions = 5,
        selectedAnswer = "Jetpack Compose",
        isNextEnabled = true,
        isLastQuestion = false
    )

    DailyQuizTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            InProgressContent(
                state = fakeState,
                onAnswerSelected = {},
                onNextClick = {}
            )
        }
    }
}