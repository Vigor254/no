package com.vigor.hotelapp.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.vigor.hotelapp.admin.AddEditHotelScreen
import com.vigor.hotelapp.admin.HomeScreenWithAdmin
import com.vigor.hotelapp.screens.BookingScreen
import com.vigor.hotelapp.screens.HotelDetailsScreen
import com.vigor.hotelapp.screens.LoginScreen
import com.vigor.hotelapp.screens.ProfileScreen
import com.vigor.hotelapp.screens.SignupScreen
import com.vigor.hotelapp.screens.SplashScreen
import com.vigor.hotelapp.viewmodel.HotelViewModel

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("home") {
            HomeScreenWithAdmin(navController)
        }
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
        composable("admin") {
            val viewModel: HotelViewModel = hiltViewModel()
            com.vigor.hotelapp.admin.AdminScreen(
                viewModel = viewModel,
                onAddHotel = { navController.navigate("add_edit_hotel") },
                onEditHotel = { hotelId -> navController.navigate("add_edit_hotel/$hotelId") },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "add_edit_hotel/{hotelId}?",
            arguments = listOf(navArgument("hotelId") { type = androidx.navigation.NavType.IntType; defaultValue = -1 })
        ) { backStackEntry ->
            val hotelId = backStackEntry.arguments?.getInt("hotelId") ?: -1
            val viewModel: HotelViewModel = hiltViewModel()
            AddEditHotelScreen(
                viewModel = viewModel,
                hotelId = hotelId,
                onSave = { navController.popBackStack() }
            )
        }
    }
}