package com.vigor.hotelapp.admin

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.vigor.hotelapp.screens.HomeScreen
import com.vigor.hotelapp.viewmodel.HotelViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenWithAdmin(
    navController: NavHostController,
    viewModel: HotelViewModel = hiltViewModel()
) {
    val isAdmin = viewModel.isAdmin.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("HotelApp") },
                actions = {
                    if (isAdmin) {
                        TextButton(onClick = { navController.navigate("admin") }) {
                            Text("Admin Panel")
                        }
                    }
                }
            )
        }
    ) { padding ->
        HomeScreen(
            navController = navController,
            viewModel = viewModel,
            modifier = Modifier.padding(padding)
        )
    }
}