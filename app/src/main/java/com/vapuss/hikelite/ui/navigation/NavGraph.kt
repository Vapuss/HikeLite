package com.vapuss.hikelite.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.vapuss.hikelite.ui.screens.MountainMapScreen
import com.vapuss.hikelite.ui.screens.TrailDetailsScreen
import com.vapuss.hikelite.viewmodel.MountainViewModel

object Routes {
    const val MAP = "map"
    const val DETAILS = "details/{mountainName}"
    fun details(mountainName: String) = "details/$mountainName"
}

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: MountainViewModel
) {
    NavHost(navController = navController, startDestination = Routes.MAP) {

        composable(Routes.MAP) {
            MountainMapScreen(
                viewModel = viewModel,
                onNavigateToDetails = { mountainName ->
                    navController.navigate(Routes.details(mountainName))
                }
            )
        }

        composable(
            route = Routes.DETAILS,
            arguments = listOf(navArgument("mountainName") { type = NavType.StringType })
        ) { backStackEntry ->
            val mountainName = backStackEntry.arguments?.getString("mountainName") ?: ""
            TrailDetailsScreen(
                viewModel = viewModel,
                mountainName = mountainName,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
