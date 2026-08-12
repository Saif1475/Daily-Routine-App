package com.dailyroutine.app.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dailyroutine.app.ui.components.BottomNavBar
import com.dailyroutine.app.ui.components.RoutineTab
import com.dailyroutine.app.ui.screens.auth.AuthScreen
import com.dailyroutine.app.ui.screens.auth.AuthViewModel
import com.dailyroutine.app.ui.screens.calendar.CalendarScreen
import com.dailyroutine.app.ui.screens.detail.DetailScreen
import com.dailyroutine.app.ui.screens.form.RoutineFormScreen
import com.dailyroutine.app.ui.screens.home.HomeScreen
import com.dailyroutine.app.ui.screens.home.RoutineViewModel
import com.dailyroutine.app.ui.screens.settings.SettingsScreen
import com.dailyroutine.app.ui.screens.stats.StatsScreen
import com.dailyroutine.app.ui.theme.Background
import com.dailyroutine.app.ui.theme.Orange

private object Routes {
    const val AUTH = "auth"
    const val HOME = "home"
    const val CALENDAR = "calendar"
    const val STATS = "stats"
    const val SETTINGS = "settings"
    const val DETAIL = "detail/{id}"
    const val ADD = "add"
    const val EDIT = "edit/{id}"

    fun detail(id: String) = "detail/$id"
    fun edit(id: String) = "edit/$id"
}

private val TAB_ROUTES = setOf(Routes.HOME, Routes.CALENDAR, Routes.STATS, Routes.SETTINGS)

@Composable
fun RoutineNavGraph() {
    val authViewModel: AuthViewModel = viewModel()
    val routineViewModel: RoutineViewModel = viewModel()
    val user by authViewModel.currentUser.collectAsState()

    if (user == null) {
        AuthScreen(authViewModel)
        return
    }

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showChrome = currentRoute == null || currentRoute in TAB_ROUTES

    Scaffold(
        containerColor = Background,
        bottomBar = {
            if (showChrome) {
                val current = when (currentRoute) {
                    Routes.CALENDAR -> RoutineTab.CALENDAR
                    Routes.STATS -> RoutineTab.STATS
                    Routes.SETTINGS -> RoutineTab.SETTINGS
                    else -> RoutineTab.HOME
                }
                BottomNavBar(current = current) { tab ->
                    val dest = when (tab) {
                        RoutineTab.HOME -> Routes.HOME
                        RoutineTab.CALENDAR -> Routes.CALENDAR
                        RoutineTab.STATS -> Routes.STATS
                        RoutineTab.SETTINGS -> Routes.SETTINGS
                    }
                    navController.navigate(dest) {
                        popUpTo(Routes.HOME) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            }
        },
        floatingActionButton = {
            if (showChrome && currentRoute != Routes.SETTINGS) {
                FloatingActionButton(
                    onClick = { navController.navigate(Routes.ADD) },
                    containerColor = Orange,
                    shape = RoundedCornerShape(18.dp),
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add routine")
                }
            }
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(bottom = if (showChrome) padding.calculateBottomPadding() else 0.dp)) {
            NavHost(navController = navController, startDestination = Routes.HOME) {
                composable(Routes.HOME) {
                    HomeScreen(
                        viewModel = routineViewModel,
                        user = user,
                        onOpenDetail = { id -> navController.navigate(Routes.detail(id)) },
                    )
                }
                composable(Routes.CALENDAR) { CalendarScreen(routineViewModel) }
                composable(Routes.STATS) { StatsScreen(routineViewModel) }
                composable(Routes.SETTINGS) {
                    SettingsScreen(user = user, onSignOut = { authViewModel.signOut() })
                }
                composable(Routes.DETAIL) { entry ->
                    val id = entry.arguments?.getString("id") ?: return@composable
                    DetailScreen(
                        routineId = id,
                        viewModel = routineViewModel,
                        onBack = { navController.popBackStack() },
                        onEdit = { editId -> navController.navigate(Routes.edit(editId)) },
                    )
                }
                composable(Routes.ADD) {
                    RoutineFormScreen(routineId = null, viewModel = routineViewModel, onDone = { navController.popBackStack() })
                }
                composable(Routes.EDIT) { entry ->
                    val id = entry.arguments?.getString("id") ?: return@composable
                    RoutineFormScreen(
                        routineId = id,
                        viewModel = routineViewModel,
                        onDone = {
                            navController.popBackStack(Routes.HOME, inclusive = false)
                        },
                    )
                }
            }
        }
    }
}
