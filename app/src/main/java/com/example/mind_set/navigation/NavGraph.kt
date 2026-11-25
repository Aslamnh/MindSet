package com.example.mind_set.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mind_set.data.local.AppDatabase
import com.example.mind_set.data.model.EmergencyIntervention
import com.example.mind_set.data.repository.DefaultEmergencyRepository
import com.example.mind_set.data.repository.EmergencyRepository
import com.example.mind_set.ui.components.BottomNavBar
import com.example.mind_set.ui.emergency.EmergencyViewModel
import com.example.mind_set.ui.screens.home.HomeScreen
import com.example.mind_set.ui.screens.jurnal.JurnalScreen
import com.example.mind_set.ui.screens.login.LoginScreen
import com.example.mind_set.ui.screens.pauseground.PauseGroundScreen
import com.example.mind_set.ui.screens.profil.ProfilScreen
import com.example.mind_set.ui.screens.progress.ProgressScreen

@Composable
fun MindSetNavGraph() {
    val rootNavController = rememberNavController()

    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val emergencyRepository = remember { DefaultEmergencyRepository(database.emergencyLogDao()) }

    NavHost(navController = rootNavController, startDestination = "login") {

        composable("login") {
            LoginScreen(onLoginSuccess = {
                rootNavController.navigate("main") {
                    popUpTo("login") { inclusive = true }
                }
            })
        }

        composable("main") {
            MainScreen(
                emergencyRepository = emergencyRepository,
                onNavigateToPauseGround = {
                    rootNavController.navigate("pause_ground")
                }
            )
        }

        composable("pause_ground") {
            val emergencyViewModel: EmergencyViewModel = viewModel(
                factory = EmergencyViewModel.provideFactory(emergencyRepository)
            )
            PauseGroundScreen(
                viewModel = emergencyViewModel,
                onBack = { rootNavController.popBackStack() }
            )
        }
    }
}

@Composable
fun MainScreen(
    emergencyRepository: EmergencyRepository,
    onNavigateToPauseGround: () -> Unit
) {
    val bottomNavController = rememberNavController()
    val emergencyHistory: List<EmergencyIntervention> by emergencyRepository
        .getEmergencyHistory()
        .collectAsState(initial = emptyList())

    Scaffold(
        bottomBar = { BottomNavBar(bottomNavController) }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(onEmergencyClick = onNavigateToPauseGround)
            }
            composable("jurnal") {
                JurnalScreen()
            }
            composable("progress") {
                ProgressScreen()
            }
            composable("profil") {
                ProfilScreen(emergencyHistory = emergencyHistory)
            }
        }
    }
}