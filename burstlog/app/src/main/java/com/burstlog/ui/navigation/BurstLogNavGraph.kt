package com.burstlog.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.burstlog.BurstLogApplication
import com.burstlog.ui.detail.RecordDetailScreen
import com.burstlog.ui.home.HomeScreen
import com.burstlog.ui.settings.SettingsScreen
import com.burstlog.ui.setup.SetupScreen
import com.burstlog.ui.timer.TimerScreen
import androidx.compose.ui.platform.LocalContext

/**
 * Top-level navigation graph.
 *
 * Defines every screen and the arguments each one accepts. Screens are
 * connected by passing lambdas (e.g., `onNavigateToSetup`) rather than
 * pulling the NavController into each screen, which keeps the screens
 * testable and decoupled.
 */
@Composable
fun BurstLogNavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val app = context.applicationContext as BurstLogApplication
    val factory = app.viewModelFactory()

    NavHost(navController = navController, startDestination = Screen.Home.route) {

        // ---------- Home ----------
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel(factory = factory),
                onStartNewWorkout = { navController.navigate(Screen.Setup.create()) },
                onPresetSelected = { presetId ->
                    navController.navigate(Screen.Setup.create(presetId))
                },
                onRecordSelected = { recordId ->
                    navController.navigate(Screen.RecordDetail.create(recordId))
                },
                onOpenSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        // ---------- Setup (with optional presetId) ----------
        composable(
            route = Screen.Setup.route,
            arguments = listOf(
                navArgument(Screen.Setup.ARG_PRESET_ID) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val presetIdString = backStackEntry.arguments?.getString(Screen.Setup.ARG_PRESET_ID)
            val presetId = presetIdString?.toLongOrNull()
            SetupScreen(
                viewModel = viewModel(factory = factory),
                presetIdToLoad = presetId,
                onBack = { navController.popBackStack() },
                onStart = { name, work, rest, rounds ->
                    navController.navigate(Screen.Timer.create(name, work, rest, rounds)) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }

        // ---------- Timer ----------
        composable(
            route = Screen.Timer.route,
            arguments = listOf(
                navArgument(Screen.Timer.ARG_NAME) { type = NavType.StringType },
                navArgument(Screen.Timer.ARG_WORK) { type = NavType.IntType },
                navArgument(Screen.Timer.ARG_REST) { type = NavType.IntType },
                navArgument(Screen.Timer.ARG_ROUNDS) { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString(Screen.Timer.ARG_NAME) ?: "Workout"
            val work = backStackEntry.arguments?.getInt(Screen.Timer.ARG_WORK) ?: 20
            val rest = backStackEntry.arguments?.getInt(Screen.Timer.ARG_REST) ?: 10
            val rounds = backStackEntry.arguments?.getInt(Screen.Timer.ARG_ROUNDS) ?: 8
            TimerScreen(
                viewModel = viewModel(factory = factory),
                workoutName = name,
                workSeconds = work,
                restSeconds = rest,
                totalRounds = rounds,
                onFinished = {
                    navController.popBackStack(Screen.Home.route, inclusive = false)
                }
            )
        }

        // ---------- Settings ----------
        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = viewModel(factory = factory),
                onBack = { navController.popBackStack() }
            )
        }

        // ---------- Record Detail ----------
        composable(
            route = Screen.RecordDetail.route,
            arguments = listOf(
                navArgument(Screen.RecordDetail.ARG_RECORD_ID) { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong(Screen.RecordDetail.ARG_RECORD_ID) ?: 0L
            RecordDetailScreen(
                viewModel = viewModel(factory = factory),
                recordId = id,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
