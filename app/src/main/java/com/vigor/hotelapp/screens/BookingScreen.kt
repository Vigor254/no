package com.vigor.hotelapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.vigor.hotelapp.viewmodel.HotelViewModel

@Composable
fun BookingScreen(
    navController: NavHostController,
    hotelId: Int,
    viewModel: HotelViewModel = hiltViewModel()
) {
    var hours by remember { mutableStateOf(1) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Book Hotel ID: $hotelId",
                style = MaterialTheme.typography.headlineMedium
            )
            IconButton(onClick = { navController.navigate("home") }) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Navigate to Home",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = hours.toString(),
            onValueChange = {
                hours = it.toIntOrNull() ?: 1
                if (hours <= 0) {
                    errorMessage = "Hours must be greater than 0"
                } else {
                    errorMessage = null
                }
            },
            label = { Text("Hours") },
            isError = errorMessage != null,
            modifier = Modifier.fillMaxWidth()
        )
        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (hours > 0) {
                    viewModel.bookHotel(hotelId, hours)
                    navController.navigate("profile")
                } else {
                    errorMessage = "Please enter valid hours"
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = hours > 0
        ) {
            Text("Confirm Booking")
        }
    }
}