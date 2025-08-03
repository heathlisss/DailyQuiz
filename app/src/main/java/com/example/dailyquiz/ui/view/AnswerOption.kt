package com.example.dailyquiz.ui.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dailyquiz.R
import com.example.dailyquiz.ui.theme.DailyQuizTheme
import com.example.dailyquiz.ui.theme.DarkPurple
import com.example.dailyquiz.ui.theme.Green
import com.example.dailyquiz.ui.theme.LightGray
import com.example.dailyquiz.ui.theme.Red

enum class AnswerState {
    DEFAULT,
    SELECTED,
    CORRECT,
    INCORRECT
}

@Composable
fun AnswerOption(
    text: String,
    state: AnswerState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val iconResId = when (state) {
        AnswerState.DEFAULT -> R.drawable.property_1_default
        AnswerState.SELECTED -> R.drawable.property_1_selected_1_
        AnswerState.CORRECT -> R.drawable.property_1_right
        AnswerState.INCORRECT -> R.drawable.property_1_wrong
    }

    val containerColor =
        if (state == AnswerState.DEFAULT) LightGray else MaterialTheme.colorScheme.surface
    val borderColor = when (state) {
        AnswerState.DEFAULT -> Color.Transparent
        AnswerState.SELECTED -> DarkPurple
        AnswerState.CORRECT -> Green
        AnswerState.INCORRECT -> Red
    }
    val contentColor = when (state) {
        AnswerState.DEFAULT -> Black
        AnswerState.SELECTED -> DarkPurple
        AnswerState.CORRECT -> Green
        AnswerState.INCORRECT -> Red
    }

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = if (state == AnswerState.DEFAULT) LightGray else MaterialTheme.colorScheme.surface,
            disabledContentColor = contentColor.copy(alpha = 1f)
        ),
        border = BorderStroke(2.dp, borderColor),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = text, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview(name = "AnswerOption - Default")
@Composable
private fun AnswerOptionDefaultPreview() {
    DailyQuizTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                Modifier.padding(
                    16.dp
                )
            ) { AnswerOption(text = "Default Answer", state = AnswerState.DEFAULT, onClick = {}) }
        }
    }
}

@Preview(name = "AnswerOption - Selected")
@Composable
private fun AnswerOptionSelectedPreview() {
    DailyQuizTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                Modifier.padding(
                    16.dp
                )
            ) { AnswerOption(text = "Selected Answer", state = AnswerState.SELECTED, onClick = {}) }
        }
    }
}

@Preview(name = "AnswerOption - Correct")
@Composable
private fun AnswerOptionCorrectPreview() {
    DailyQuizTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                Modifier.padding(
                    16.dp
                )
            ) { AnswerOption(text = "Correct Answer", state = AnswerState.CORRECT, onClick = {}) }
        }
    }
}

@Preview(name = "AnswerOption - Incorrect")
@Composable
private fun AnswerOptionIncorrectPreview() {
    DailyQuizTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                Modifier.padding(
                    16.dp
                )
            ) {
                AnswerOption(
                    text = "Incorrect Answer",
                    state = AnswerState.INCORRECT,
                    onClick = {})
            }
        }
    }
}