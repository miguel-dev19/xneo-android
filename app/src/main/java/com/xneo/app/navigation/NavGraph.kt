package com.xneo.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.xneo.app.ui.screens.auth.LoginScreen
import com.xneo.app.ui.screens.auth.RegisterScreen
import com.xneo.app.ui.screens.home.HomeScreen
import com.xneo.app.ui.screens.player.PlayerScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController = navController) }
        composable("player/{videoId}", arguments = listOf(navArgument("videoId") { type = NavType.StringType })) { entry ->
            PlayerScreen(videoId = entry.arguments?.getString("videoId") ?: "", navController = navController)
        }
        composable("login") { LoginScreen(navController = navController) }
        composable("register") { RegisterScreen(navController = navController) }
    }
}
