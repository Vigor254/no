package com.vigor.hotelapp.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.vigor.hotelapp.model.Hotel
import com.vigor.hotelapp.viewmodel.HotelViewModel
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(navController: NavHostController, viewModel: HotelViewModel = hiltViewModel()) {
    val hotels by remember { mutableStateOf(viewModel.hotels.value) }
    var currentIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            if (hotels.isNotEmpty()) {
                currentIndex = (currentIndex + 1) % hotels.size
            }
        }
    }

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
                text = "Hotel App",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile",
                modifier = Modifier
                    .size(32.dp)
                    .clickable {
                        if (viewModel.currentUser.value == null) {
                            navController.navigate("login")
                        } else {
                            navController.navigate("profile")
                        }
                    },
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (hotels.isNotEmpty()) {
            val hotel = hotels.getOrNull(currentIndex) ?: hotels.firstOrNull() ?: run {
                Text("No hotels available", style = MaterialTheme.typography.bodyLarge)
                return@Column
            }
            HotelCarouselItem(
                hotel = hotel,
                onBookClick = {
                    if (viewModel.currentUser.value == null) {
                        navController.navigate("login")
                    } else {
                        navController.navigate("booking?hotelId=${hotel.id}")
                    }
                },
                onSeeMoreClick = {
                    navController.navigate("hotelDetails?hotelId=${hotel.id}")
                }
            )
        } else {
            Text("Loading hotels...", style = MaterialTheme.typography.bodyLarge)
            LaunchedEffect(Unit) {
                try {
                    viewModel.loadHotels()
                } catch (e: Exception) {
                    Log.e("HomeScreen", "Failed to load hotels", e)
                }
            }
        }

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

@Composable
fun HotelCarouselItem(hotel: Hotel, onBookClick: () -> Unit, onSeeMoreClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 400.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Image(
                painter = runCatching {
                    if (hotel.imageResId != 0) painterResource(id = hotel.imageResId)
                    else painterResource(id = android.R.drawable.ic_menu_report_image)
                }.getOrNull() ?: painterResource(id = android.R.drawable.ic_menu_report_image),
                contentDescription = hotel.name.takeIf { it.isNotBlank() } ?: "Hotel Image", // Safe content description
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = hotel.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = hotel.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Location: ${hotel.location}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Price: $${hotel.pricePerHour}/hour",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onBookClick,
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(text = "Book Now", color = Color.White)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "See More",
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable(onClick = onSeeMoreClick),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp
            )
        }
    }
}