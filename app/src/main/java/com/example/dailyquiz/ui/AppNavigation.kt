package com.example.dailyquiz.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dailyquiz.ui.view.HistoryScreen
import com.example.dailyquiz.ui.view.QuizScreen
import com.example.dailyquiz.ui.view.ResultsScreen

object AppDestinations {
    const val QUIZ_ROUTE = "quiz"
    const val RESULTS_ROUTE = "results"
    const val HISTORY_ROUTE = "history"
    const val ATTEMPT_ID_ARG = "attemptId"
    const val START_WITH_REVIEW_ARG = "startWithReview"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppDestinations.QUIZ_ROUTE
    ) {
        composable(AppDestinations.QUIZ_ROUTE) {
            QuizScreen(
                onQuizFinished = { attemptId, _, _ ->
                    navController.navigate(
                        "${AppDestinations.RESULTS_ROUTE}/$attemptId?${AppDestinations.START_WITH_REVIEW_ARG}=false"
                    ) {
                        popUpTo(AppDestinations.QUIZ_ROUTE) { inclusive = true }
                    }
                },
                onHistoryClicked = {
                    navController.navigate(AppDestinations.HISTORY_ROUTE)
                }
            )
        }

        composable(
            route = "${AppDestinations.RESULTS_ROUTE}/{${AppDestinations.ATTEMPT_ID_ARG}}" +
                    "?${AppDestinations.START_WITH_REVIEW_ARG}={${AppDestinations.START_WITH_REVIEW_ARG}}",
            arguments = listOf(
                navArgument(AppDestinations.ATTEMPT_ID_ARG) { type = NavType.LongType },
                navArgument(AppDestinations.START_WITH_REVIEW_ARG) {
                    type = NavType.BoolType; defaultValue = false
                }
            )
        ) {
            ResultsScreen(
                onRestart = {
                    navController.navigate(AppDestinations.QUIZ_ROUTE) {
                        popUpTo(0)
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(AppDestinations.HISTORY_ROUTE) {
            HistoryScreen(
                onBack = { navController.popBackStack() },
                onNavigateToReview = { attemptId ->
                    navController.navigate(
                        "${AppDestinations.RESULTS_ROUTE}/$attemptId?${AppDestinations.START_WITH_REVIEW_ARG}=true"
                    )
                }
            )
        }
    }
}