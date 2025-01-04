package it.reply.open.trimoji.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import it.reply.open.trimoji.ui.screen.questions.GameScreen
import it.reply.open.trimoji.ui.screen.results.ResultsScreen
import it.reply.open.trimoji.ui.screen.splash.SplashScreen
import kotlinx.serialization.Serializable


sealed interface TrimojiDestination {
    @Composable
    fun Content(navController: NavController)
}

inline fun <reified T : TrimojiDestination> NavGraphBuilder.addDestinationToGraph(navController: NavController) {
    composable<T> { backStackEntry ->
        backStackEntry.toRoute<T>().Content(navController)
    }
}

sealed interface TrimojiGraph {
    @Serializable
    data object Splash : TrimojiDestination {
        @Composable
        override fun Content(navController: NavController) {
            SplashScreen(
                onDoneLoading = {
                    navController.navigate(Game(10))
                }
            )
        }
    }

    //TODO home

    @Serializable
    data class Game(
        val questionAmount: Int,
    ) : TrimojiDestination {
        @Composable
        override fun Content(navController: NavController) {
            GameScreen(
                onDone = { correctAnswersCount ->
                    navController.navigate(Results(amount = correctAnswersCount)) {
                        popUpTo<Splash>()
                    }
                },
                onAbort = {
                    navController.navigateUp()
                }
            )
        }
    }

    @Serializable
    data class Results(val amount: Int) : TrimojiDestination {
        @Composable
        override fun Content(navController: NavController) {
            ResultsScreen(
                correctAnswers = amount,
                questions = 10,
                onBack = {
                    navController.navigateUp()
                }
            )
        }

    }
}



