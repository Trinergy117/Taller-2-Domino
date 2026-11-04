package com.udistrital.taller2_domino.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.udistrital.taller2_domino.ui.auth.AuthViewModel
import com.udistrital.taller2_domino.ui.auth.LoginScreen
import com.udistrital.taller2_domino.ui.lobby.LobbyScreen

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = viewModel()
) {
    val navController = rememberNavController()
    val currentUser by authViewModel.currentUserFlow.collectAsStateWithLifecycle()

    val startDestination = if (currentUser != null) "menu" else "login"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("menu") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                authViewModel = authViewModel
            )
        }
        composable("menu") {
            MenuScreen(
                userEmail = currentUser?.email ?: if (currentUser?.isAnonymous == true) "Invitado" else null,
                onNewGame = { navController.navigate("game") },
                onOnlineGame = { navController.navigate("lobby") },
                onSavedGames = { navController.navigate("saved") },
                onSignOut = {
                    authViewModel.signOut()
                    navController.navigate("login") {
                        popUpTo("menu") { inclusive = true }
                    }
                }
            )
        }
        composable("lobby") {
            LobbyScreen(
                currentUser = currentUser,
                onRoomJoined = { roomCode ->
                    navController.navigate("game/$roomCode") {
                        popUpTo("lobby") { inclusive = true }
                    }
                },
                onBackToMenu = {
                    navController.popBackStack()
                }
            )
        }
        composable("game") {
            GameScreen(
                roomCode = null,
                localUserId = currentUser?.uid ?: "p1",
                onBackToMenu = { navController.navigate("menu") { popUpTo("menu") { inclusive = true } } }
            )
        }
        composable(
            route = "game/{roomCode}",
            arguments = listOf(navArgument("roomCode") { type = NavType.StringType })
        ) { backStackEntry ->
            val roomCode = backStackEntry.arguments?.getString("roomCode")
            GameScreen(
                roomCode = roomCode,
                localUserId = currentUser?.uid ?: "",
                onBackToMenu = { navController.navigate("menu") { popUpTo("menu") { inclusive = true } } }
            )
        }
        composable("saved") { Text("Partidas guardadas") }
    }
}

@Preview
@Composable
fun AppNavigationPreview() {
    AppNavigation()
}
