package it.reply.open.trimoji.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import it.reply.open.trimoji.ui.screen.questions.GameScreen
import it.reply.open.trimoji.ui.screen.results.ResultsScreen
import it.reply.open.trimoji.ui.screen.splash.SplashScreen
import kotlinx.serialization.Serializable

sealed interface TrimojiGraph {
    @Serializable
    data object Splash

    @Serializable
    data object Home

    @Serializable
    data class Game(val questionAmount: Int)

    @Serializable
    data class Results(val correctAnswersCount: Int)
}


@Composable
fun TrimojiNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = TrimojiGraph.Splash,
    ) {
        composable<TrimojiGraph.Splash> {
            SplashScreen(
                onDoneLoading = {
                    navController.navigate(TrimojiGraph.Game(10))
                }
            )
        }
        composable<TrimojiGraph.Game> {
            GameScreen(
                onDone = { correctAnswersCount ->
                    navController.navigate(TrimojiGraph.Results(correctAnswersCount = correctAnswersCount)) {
                        popUpTo<TrimojiGraph.Splash>()
                    }
                },
                onAbort = {
                    navController.navigateUp()
                }
            )


        }
        composable<TrimojiGraph.Results> { backStackEntry ->
            val correctAnswersCount = backStackEntry.toRoute<TrimojiGraph.Results>().correctAnswersCount
            ResultsScreen(
                correctAnswers = correctAnswersCount,
                questions = 10,
                onBack = {
                    navController.navigateUp()
                }
            )
        }
    }
}