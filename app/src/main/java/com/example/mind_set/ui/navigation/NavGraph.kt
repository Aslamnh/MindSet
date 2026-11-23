package com.example.mind_set.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mind_set.ui.components.BottomNavBar
import com.example.mind_set.ui.screens.home.HomeScreen
import com.example.mind_set.ui.screens.jurnal.JurnalScreen
import com.example.mind_set.ui.screens.login.LoginScreen
import com.example.mind_set.ui.screens.pauseground.PauseGroundScreen
import com.example.mind_set.ui.screens.profil.ProfilScreen
import com.example.mind_set.ui.screens.progress.ProgressScreen

@Composable
fun MindSetNavGraph() {
    val rootNavController = rememberNavController()

    NavHost(navController = rootNavController, startDestination = "login") {

        composable("login") {
            LoginScreen(onLoginSuccess = { rootNavController.navigate("main") {
                popUpTo("login") { inclusive = true }
            }})
        }

        composable("main") {
            MainScreen(onNavigateToPauseGround = {
                rootNavController.navigate("pause_ground")
            })
        }

        composable("pause_ground") {
            PauseGroundScreen(onBack = { rootNavController.popBackStack() })
        }
    }
}

@Composable
fun MainScreen(onNavigateToPauseGround: () -> Unit) {
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavBar(bottomNavController) }
    ) { innerPadding ->
        NavHost(
            navController    = bottomNavController,
            startDestination = "home",
            modifier         = Modifier.padding(innerPadding)
        ) {
            composable("home")     { HomeScreen(onEmergencyClick = onNavigateToPauseGround) }
            composable("jurnal")   { JurnalScreen() }
            composable("progress") { ProgressScreen() }
            composable("profil")   { ProfilScreen() }
        }
    }
}
