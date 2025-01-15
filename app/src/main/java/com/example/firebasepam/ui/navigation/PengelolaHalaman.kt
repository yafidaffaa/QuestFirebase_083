package com.example.firebasepam.ui.navigation

import DetailScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.firebasepam.ui.view.HomeScreen
import com.example.firebasepam.ui.view.InsertMhsView
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument


@Composable
fun PengelolaHalaman(
    modifier: Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = DestinasiHome.route,
        modifier = Modifier
    ) {
        composable(DestinasiHome.route) {
            HomeScreen(
                navigateToItemEntry = {
                    navController.navigate(DestinasiInsert.route)
                },
                onDetailClick = {
                    navController.navigate("${DestinasiDetail.route}/$it")
                }
            )
        }
        composable(DestinasiInsert.route) {
            InsertMhsView(
                onBack = {
                    navController.popBackStack()
                },
                onNavigate = {
                    navController.navigate(DestinasiHome.route)
                }
            )
        }
        composable(
            route = "detail/{nim}",
            arguments = listOf(navArgument("nim") { type = NavType.StringType })
        ) { backStackEntry ->
            val nim = backStackEntry.arguments?.getString("nim") ?: ""
            DetailScreen(
                nim = nim,
                onNavigateBack = { navController.navigateUp() },
                onEditClick = { /* handle edit */ },
                onDeleteSuccess = {
                    navController.navigateUp()
                }
            )
        }
    }
}
