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
import com.example.mind_set.data.remote.GeminiAiService
import com.example.mind_set.data.repository.DefaultAiRecommendationRepository
import com.example.mind_set.data.repository.DefaultEmergencyRepository
import com.example.mind_set.data.repository.DefaultJournalRepository
import com.example.mind_set.data.repository.DefaultProgressRepository
import com.example.mind_set.data.repository.DefaultUserRepository
import com.example.mind_set.data.repository.UserRepository
import com.example.mind_set.ui.components.BottomNavBar
import com.example.mind_set.ui.emergency.EmergencyViewModel
import com.example.mind_set.ui.screens.home.HomeScreen
import com.example.mind_set.ui.screens.home.HomeViewModel
import com.example.mind_set.ui.screens.jurnal.JournalViewModel
import com.example.mind_set.ui.screens.jurnal.JurnalScreen
import com.example.mind_set.ui.screens.login.LoginScreen
import com.example.mind_set.ui.screens.login.LoginViewModel
import com.example.mind_set.ui.screens.pauseground.PauseGroundScreen
import com.example.mind_set.ui.screens.profil.ProfilScreen
import com.example.mind_set.ui.screens.progress.ProgressScreen
import com.example.mind_set.ui.screens.progress.ProgressViewModel
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun MindSetNavGraph() {
    val rootNavController = rememberNavController()

    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val userRepository = remember { DefaultUserRepository(database.userDao()) }

    NavHost(navController = rootNavController, startDestination = "login") {

        composable("login") {
            val loginViewModel: LoginViewModel = viewModel(
                factory = LoginViewModel.provideFactory(userRepository)
            )
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = { username ->
                    val encoded = URLEncoder.encode(username, StandardCharsets.UTF_8.toString())
                    rootNavController.navigate("main/$encoded") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "main/{encodedUsername}",
            arguments = listOf(navArgument("encodedUsername") { type = NavType.StringType })
        ) { backStackEntry ->
            val encoded = backStackEntry.arguments?.getString("encodedUsername") ?: "Alex"
            val username = URLDecoder.decode(encoded, StandardCharsets.UTF_8.toString())
            MainScreen(
                currentUsername = username,
                database = database,
                userRepository = userRepository,
                onNavigateToPauseGround = {
                    rootNavController.navigate("pause_ground/$encoded")
                },
                onLogout = {
                    rootNavController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "pause_ground/{encodedUsername}",
            arguments = listOf(navArgument("encodedUsername") { type = NavType.StringType })
        ) { backStackEntry ->
            val encoded = backStackEntry.arguments?.getString("encodedUsername") ?: "default_user"
            val username = URLDecoder.decode(encoded, StandardCharsets.UTF_8.toString())
            val ctx = LocalContext.current
            val emergencyViewModel: EmergencyViewModel = viewModel(
                factory = EmergencyViewModel.provideFactory(
                    DefaultEmergencyRepository(
                        emergencyLogDao = AppDatabase.getDatabase(ctx).emergencyLogDao(),
                        context = ctx,
                        geminiAiService = GeminiAiService(),
                        username = username
                    )
                )
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
    database: AppDatabase,
    userRepository: UserRepository,
    onNavigateToPauseGround: () -> Unit,
    onLogout: () -> Unit
) {
    val bottomNavController = rememberNavController()
    val context = LocalContext.current
    val geminiAiService = remember { GeminiAiService() }

    val emergencyRepository = remember(currentUsername) {
        DefaultEmergencyRepository(
            emergencyLogDao = database.emergencyLogDao(),
            context = context,
            geminiAiService = geminiAiService,
            username = currentUsername
        )
    }
    val journalRepository = remember(currentUsername) {
        DefaultJournalRepository(
            journalDao = database.journalDao(),
            context = context,
            geminiAiService = geminiAiService,
            username = currentUsername
        )
    }
    val aiRecommendationRepository = remember(currentUsername) {
        DefaultAiRecommendationRepository(
            context = context,
            journalDao = database.journalDao(),
            emergencyLogDao = database.emergencyLogDao(),
            aiRecommendationDao = database.aiRecommendationDao(),
            geminiAiService = geminiAiService,
            username = currentUsername
        )
    }

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
                val homeViewModel: HomeViewModel = viewModel(
                    factory = HomeViewModel.provideFactory(aiRecommendationRepository)
                )
                HomeScreen(
                    currentUsername = currentUsername,
                    viewModel = homeViewModel,
                    onEmergencyClick = onNavigateToPauseGround,
                    onNavigateToJurnal = { bottomNavController.navigate("jurnal") }
                )
            }
            composable("jurnal") {
                val journalViewModel: JournalViewModel = viewModel(
                    factory = JournalViewModel.provideFactory(journalRepository)
                )
                JurnalScreen(viewModel = journalViewModel)
            }
            composable("progress") {
                val progressRepository = remember(currentUsername) {
                    DefaultProgressRepository(
                        journalDao = database.journalDao(),
                        emergencyLogDao = database.emergencyLogDao(),
                        checkInDao = database.dailyCheckInDao(),
                        relapseLogDao = database.relapseLogDao(),
                        username = currentUsername
                    )
                }
                val progressViewModel: ProgressViewModel = viewModel(
                    factory = ProgressViewModel.provideFactory(progressRepository)
                )
                ProgressScreen(viewModel = progressViewModel)
            }
            composable("profil") {
                ProfilScreen(
                    currentUsername = currentUsername,
                    emergencyHistory = emergencyHistory,
                    journalHistory = journalHistory,
                    userRepository = userRepository,
                    onLogout = onLogout
                )
            }
        }
    }
}
