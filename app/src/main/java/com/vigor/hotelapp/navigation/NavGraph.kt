package com.vigor.hotelapp.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.vigor.hotelapp.screens.*

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignupScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        composable(
            route = "booking?hotelId={hotelId}",
            arguments = listOf(navArgument("hotelId") { type = androidx.navigation.NavType.IntType })
        ) { backStackEntry ->
            val hotelId = backStackEntry.arguments?.getInt("hotelId") ?: 0
            if (hotelId > 0) {
                BookingScreen(navController = navController, hotelId = hotelId)
            } else {
                Text("Invalid hotel ID", modifier = Modifier.fillMaxSize())
            }
        }
        composable(
            route = "hotelDetails?hotelId={hotelId}",
            arguments = listOf(navArgument("hotelId") { type = androidx.navigation.NavType.IntType })
        ) { backStackEntry ->
            val hotelId = backStackEntry.arguments?.getInt("hotelId") ?: 0
            if (hotelId > 0) {
                HotelDetailsScreen(navController = navController, hotelId = hotelId)
            } else {
                Text("Invalid hotel ID", modifier = Modifier.fillMaxSize())
            }
        }
    }
}