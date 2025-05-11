package com.vigor.hotelapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.vigor.hotelapp.screens.*

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignupScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        composable(
            "booking?hotelId={hotelId}",
            arguments = listOf(navArgument("hotelId") { type = androidx.navigation.NavType.IntType })
        ) { backStackEntry ->
            BookingScreen(
                navController = navController,
                hotelId = backStackEntry.arguments?.getInt("hotelId") ?: 0
            )
        }
        composable(
            "hotelDetails?hotelId={hotelId}",
            arguments = listOf(navArgument("hotelId") { type = androidx.navigation.NavType.IntType })
        ) { backStackEntry ->
            HotelDetailsScreen(
                navController = navController,
                hotelId = backStackEntry.arguments?.getInt("hotelId") ?: 0
            )
        }
        composable(
            "adminLogin",
            deepLinks = listOf(navDeepLink { uriPattern = "hotelapp://admin" })
        ) { AdminLoginScreen(navController) }
        composable("admin") { AdminScreen(navController) }
    }
}