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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mind_set.data.local.AppDatabase
import com.example.mind_set.data.model.EmergencyIntervention
import com.example.mind_set.data.model.JournalItem
import com.example.mind_set.data.repository.DefaultEmergencyRepository
import com.example.mind_set.data.repository.DefaultJournalRepository
import com.example.mind_set.data.repository.DefaultUserRepository
import com.example.mind_set.data.repository.EmergencyRepository
import com.example.mind_set.data.repository.JournalRepository
import com.example.mind_set.ui.components.BottomNavBar
import com.example.mind_set.ui.emergency.EmergencyViewModel
import com.example.mind_set.ui.screens.home.HomeScreen
import com.example.mind_set.ui.screens.jurnal.JournalViewModel
import com.example.mind_set.ui.screens.jurnal.JurnalScreen
import com.example.mind_set.ui.screens.login.LoginScreen
import com.example.mind_set.ui.screens.login.LoginViewModel
import com.example.mind_set.ui.screens.pauseground.PauseGroundScreen
import com.example.mind_set.ui.screens.profil.ProfilScreen
import com.example.mind_set.ui.screens.progress.ProgressScreen

@Composable
fun MindSetNavGraph() {
    val rootNavController = rememberNavController()

    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val emergencyRepository = remember { DefaultEmergencyRepository(database.emergencyLogDao()) }
    val journalRepository = remember { DefaultJournalRepository(database.journalDao()) }
    val userRepository = remember { DefaultUserRepository(database.userDao()) }

    NavHost(navController = rootNavController, startDestination = "login") {

        composable("login") {
            val loginViewModel: LoginViewModel = viewModel(
                factory = LoginViewModel.provideFactory(userRepository)
            )
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = { username ->
                    rootNavController.navigate("main/$username") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "main/{username}",
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: "Alex"
            MainScreen(
                currentUsername = username,
                emergencyRepository = emergencyRepository,
                journalRepository = journalRepository,
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
    currentUsername: String,
    emergencyRepository: EmergencyRepository,
    journalRepository: JournalRepository,
    onNavigateToPauseGround: () -> Unit
) {
    val bottomNavController = rememberNavController()
    val emergencyHistory: List<EmergencyIntervention> by emergencyRepository
        .getEmergencyHistory()
        .collectAsState(initial = emptyList())

    val journalHistory: List<JournalItem> by journalRepository
        .getAllJournals()
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
                HomeScreen(
                    currentUsername = currentUsername,
                    onEmergencyClick = onNavigateToPauseGround
                )
            }
            composable("jurnal") {
                val journalViewModel: JournalViewModel = viewModel(
                    factory = JournalViewModel.provideFactory(journalRepository)
                )
                JurnalScreen(viewModel = journalViewModel)
            }
            composable("progress") {
                ProgressScreen()
            }
            composable("profil") {
                ProfilScreen(
                    currentUsername = currentUsername,
                    emergencyHistory = emergencyHistory,
                    journalHistory = journalHistory
                )
            }
        }
    }
}
