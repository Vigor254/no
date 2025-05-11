package com.vigor.hotelapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.vigor.hotelapp.viewmodel.HotelViewModel
import android.util.Log

@Composable
fun HotelDetailsScreen(
    navController: NavHostController,
    hotelId: Int,
    viewModel: HotelViewModel = hiltViewModel()
) {
    val hotel = viewModel.hotels.value.find { it.id == hotelId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        hotel?.let {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Image(
                        painter = if (it.imageResId != 0) {
                            painterResource(id = it.imageResId)
                        } else {
                            Log.w("HotelDetails", "Using fallback image for hotel ${it.name} with invalid imageResId")
                            painterResource(id = android.R.drawable.ic_menu_report_image)
                        },
                        contentDescription = it.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = it.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = it.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Location: ${it.location}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Price: $${it.pricePerHour}/hour",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (viewModel.currentUser.value == null) {
                                navController.navigate("login")
                            } else {
                                navController.navigate("booking?hotelId=${it.id}")
                            }
                        },
                        modifier = Modifier.align(Alignment.End),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(text = "Book Now", color = Color.White)
                    }
                }
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