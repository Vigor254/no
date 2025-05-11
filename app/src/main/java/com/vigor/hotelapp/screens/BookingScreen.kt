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
fun BookingScreen(navController: NavHostController, hotelId: Int, viewModel: HotelViewModel = hiltViewModel()) {
    var hours by remember { mutableStateOf(1) }

    val hotel = viewModel.hotels.value.find { it.id == hotelId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        hotel?.let {
            Text(text = "Book ${it.name}", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Price: $${it.pricePerHour}/hour")
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = hours.toString(),
                onValueChange = { hours = it.toIntOrNull() ?: 1 },
                label = { Text("Hours") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.bookHotel(hotelId, hours)
                    navController.navigate("profile") {
                        popUpTo("booking") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirm Booking")
            }
        } ?: Text(text = "Hotel not found", color = MaterialTheme.colorScheme.error)

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}