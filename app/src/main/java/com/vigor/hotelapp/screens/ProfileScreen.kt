package com.vigor.hotelapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.vigor.hotelapp.viewmodel.HotelViewModel

@Composable
fun ProfileScreen(navController: NavHostController, viewModel: HotelViewModel = hiltViewModel()) {
    val user = viewModel.currentUser.value
    val bookings = viewModel.bookings.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Profile: ${user?.email ?: "Guest"}",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.logout()
                navController.navigate("login") {
                    popUpTo("profile") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Your Bookings", style = MaterialTheme.typography.titleMedium)

        if (bookings.isEmpty()) {
            Text("No bookings found")
        } else {
            LazyColumn {
                items(bookings) { booking ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Hotel ID: ${booking.hotelId}")
                            Text("Hours: ${booking.hours}")
                            Text("Total Cost: $${booking.totalCost}")
                            Text("Date: ${booking.bookingDate}")
                        }
                    }
                }
            }
        }
    }
}