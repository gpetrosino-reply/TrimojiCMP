package it.reply.open.trimoji

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import it.reply.open.trimoji.navigation.TrimojiGraph
import it.reply.open.trimoji.navigation.addDestinationToGraph
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext

@Composable
@Preview
fun App() {
    MaterialTheme {
        KoinContext {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = TrimojiGraph.Splash
            ) {
                addDestinationToGraph<TrimojiGraph.Splash>(navController)
                addDestinationToGraph<TrimojiGraph.Questions>(navController)
                addDestinationToGraph<TrimojiGraph.Results>(navController)
            }
        }
    }
}