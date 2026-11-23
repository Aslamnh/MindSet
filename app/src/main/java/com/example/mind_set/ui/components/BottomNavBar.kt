package com.example.mind_set.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mind_set.ui.theme.MindSetNeutralGrey
import com.example.mind_set.ui.theme.MindSetPrimary

data class BottomNavItem(
    val label: String,
    val route: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem("Beranda",  "home",     Icons.Filled.Home),
    BottomNavItem("Jurnal",   "jurnal",   Icons.AutoMirrored.Filled.MenuBook),
    BottomNavItem("Progress", "progress", Icons.Filled.BarChart),
    BottomNavItem("Profil",   "profil",   Icons.Filled.Person)
)

@Composable
fun BottomNavBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick  = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo("home") { saveState = true }
                            launchSingleTop = true
                            restoreState    = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector    = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(item.label, style = MaterialTheme.typography.labelSmall)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = MindSetPrimary,
                    selectedTextColor   = MindSetPrimary,
                    unselectedIconColor = MindSetNeutralGrey,
                    unselectedTextColor = MindSetNeutralGrey,
                    indicatorColor      = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}
