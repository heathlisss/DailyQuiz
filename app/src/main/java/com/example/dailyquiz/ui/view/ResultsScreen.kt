package com.example.dailyquiz.ui.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dailyquiz.R
import com.example.dailyquiz.data.model.QuizReview
import com.example.dailyquiz.data.model.ReviewedQuestion
import com.example.dailyquiz.ui.AnswerOption
import com.example.dailyquiz.ui.AnswerState
import com.example.dailyquiz.ui.theme.DailyQuizTheme
import com.example.dailyquiz.ui.theme.DarkPurple
import com.example.dailyquiz.ui.theme.Gray
import com.example.dailyquiz.ui.theme.Purple
import com.example.dailyquiz.ui.theme.White
import com.example.dailyquiz.ui.theme.Yellow
import com.example.dailyquiz.viewmodel.ResultsViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(onRestart: () -> Unit, onBack: () -> Unit) {
    val viewModel: ResultsViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Результаты",
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = state.canToggleReview) { viewModel.onToggleReview() },
                    shape = RoundedCornerShape(40.dp),
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
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        StarRating(score = state.correctAnswersCount)
                        Text(
                            text = "${state.correctAnswersCount} из ${state.totalQuestions}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Yellow
                        )
                        Text(
                            text = state.resultTitle,
                            style = MaterialTheme.typography.headlineLarge,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = state.resultSubtitle,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onRestart,
                            modifier = Modifier.fillMaxWidth(0.9f),
                            shape = RoundedCornerShape(13.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Purple,
                                contentColor = White
                            )
                        ) {
                            Text(
                                text = "НАЧАТЬ ЗАНОВО",
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.padding(6.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }

            item {
                AnimatedVisibility(visible = state.isReviewVisible) {
                    QuizReviewContent(quizReview = state.quizReview)
                }
            }

            if (state.canToggleReview && state.isReviewVisible) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onRestart,
                        modifier = Modifier.fillMaxWidth(0.9f),
                        shape = RoundedCornerShape(13.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = DarkPurple
                        )
                    ) {
                        Text(
                            text = "НАЧАТЬ ЗАНОВО",
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun QuizReviewContent(quizReview: QuizReview?) {
    if (quizReview == null) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
        }
    } else {
        Column(
            modifier = Modifier.padding(top = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            quizReview.questions.forEachIndexed { index, question ->
                QuizReviewCard(
                    question = question,
                    questionNumber = index + 1,
                    totalQuestions = quizReview.questions.size
                )
            }
        }
    }
}

@Composable
private fun QuizReviewCard(
    question: ReviewedQuestion,
    questionNumber: Int,
    totalQuestions: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(40.dp),
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Вопрос $questionNumber из $totalQuestions",
                style = MaterialTheme.typography.bodyLarge,
                color = Gray,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = question.questionText,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 3
                )
            }

            question.allAnswers.forEach { answer ->
                val state = when {
                    answer == question.correctAnswer -> AnswerState.CORRECT
                    answer == question.userAnswer -> AnswerState.INCORRECT
                    else -> AnswerState.DEFAULT
                }
                AnswerOption(
                    text = answer,
                    state = state,
                    onClick = {},
                    enabled = false
                )
            }
        }
    }
}

@Composable
private fun StarRating(score: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(5) { index ->
            val starId =
                if (index < score) R.drawable.property_1_active else R.drawable.property_1_inactive
            Image(
                painter = painterResource(id = starId),
                contentDescription = null,
                modifier = Modifier.size(46.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ResultsScreenPreview() {
    DailyQuizTheme {
        ResultsScreen(onRestart = {}, onBack = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun QuizReviewCardPreview() {
    val fakeReviewedQuestion = ReviewedQuestion(
        questionText = "Какой из этих городов является столицей Австралии?",
        allAnswers = listOf("Сидней", "Мельбурн", "Канберра", "Перт"),
        correctAnswer = "Канберра",
        userAnswer = "Сидней",
        wasCorrect = false
    )
    DailyQuizTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            QuizReviewCard(
                question = fakeReviewedQuestion,
                questionNumber = 4,
                totalQuestions = 5
            )
        }
    }
}