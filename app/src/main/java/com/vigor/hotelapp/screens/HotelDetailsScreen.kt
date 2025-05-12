package com.vigor.hotelapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.vigor.hotelapp.viewmodel.HotelViewModel

@Composable
fun HotelDetailsScreen(
    navController: NavHostController,
    hotelId: Int,
    viewModel: HotelViewModel = hiltViewModel()
) {
    val hotels by viewModel.hotels
    val hotel = hotels.find { it.id == hotelId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = hotel?.name ?: "Hotel Details",
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
        if (hotel != null) {
            Image(
                painter = painterResource(id = hotel.imageResId),
                contentDescription = hotel.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = hotel.description,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Location: ${hotel.location}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Price: $${hotel.pricePerHour}/hour",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.navigate("booking?hotelId=$hotelId") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Book Now")
            }
        } else {
            Text(
                text = "Hotel not found",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}