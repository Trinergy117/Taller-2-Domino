package com.udistrital.taller2_domino.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "menu") {
        composable("menu") {
            MenuScreen(
                onNewGame = { navController.navigate("game") },
                onSavedGames = { navController.navigate("saved") }
            )
        }
        composable("game") { GameScreen() }
        composable("saved") { Text("Partidas guardadas") }
    }
}

@Preview
@Composable
fun AppNavigationPreview(){
    AppNavigation()
}