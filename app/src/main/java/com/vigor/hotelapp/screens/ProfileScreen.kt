package com.vigor.hotelapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.vigor.hotelapp.model.Booking
import com.vigor.hotelapp.viewmodel.HotelViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModel: HotelViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val user = viewModel.currentUser.collectAsState().value
    val isAdmin = viewModel.isAdmin.collectAsState().value
    val bookings = viewModel.bookings.collectAsState().value

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Profile"
        )
        if (user != null) {
            Text(text = "Name: ${user.fullName}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Email: ${user.email}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))
            if (isAdmin) {
                Button(onClick = { navController.navigate("admin") }) {
                    Text("Go to Admin Panel")
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            Text(
                text = "Your Bookings",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            if (bookings.isEmpty()) {
                Text(text = "No bookings found", style = MaterialTheme.typography.bodyMedium)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(bookings) { booking ->
                        BookingCard(booking)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                viewModel.logout()
                navController.navigate("login") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }) {
                Text("Logout")
            }
        } else {
            Text(text = "No user logged in", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate("login") }) {
                Text("Go to Login")
            }
        }
    }
}

@Composable
fun BookingCard(booking: Booking) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Hotel ID: ${booking.hotelId}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Hours: ${booking.hours}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Total Cost: $${booking.totalCost}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Date: ${booking.bookingDate}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Phone: ${booking.phoneNumber}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Notes: ${booking.notes}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}