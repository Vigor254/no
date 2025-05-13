package com.vigor.hotelapp.screens

import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.vigor.hotelapp.R
import com.vigor.hotelapp.model.Hotel
import com.vigor.hotelapp.viewmodel.HotelViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HotelViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val hotels = viewModel.hotels.collectAsState()
    val user = viewModel.currentUser.collectAsState().value
    val isAdmin = viewModel.isAdmin.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("HotelApp") },
                actions = {
                    if (isAdmin) {
                        IconButton(onClick = { navController.navigate("admin") }) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Admin Panel"
                            )
                        }
                    }
                    IconButton(onClick = {
                        if (user != null) {
                            navController.navigate("profile")
                        } else {
                            navController.navigate("login")
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (hotels.value.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(hotels.value) { hotel ->
                        HotelCard(
                            hotel = hotel,
                            onSeeMoreClick = {
                                navController.navigate("hotelDetails?hotelId=${hotel.id}")
                            },
                            onBookClick = {
                                if (user != null) {
                                    navController.navigate("booking?hotelId=${hotel.id}")
                                } else {
                                    navController.navigate("login")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HotelCard(hotel: Hotel, onSeeMoreClick: () -> Unit, onBookClick: () -> Unit) {
    Log.d("HotelCard", "Displaying hotel: ${hotel.name}, imageResId: ${hotel.imageResId}")
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val imageResId = try {
                hotel.imageResId.takeIf { it != 0 && it != -1 } ?: R.drawable.hotel1
            } catch (e: Exception) {
                Log.e("HotelCard", "Invalid imageResId: ${hotel.imageResId}, using fallback")
                R.drawable.hotel1
            }
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = hotel.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = hotel.name,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
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
                text = "$${hotel.pricePerHour}/hour",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = onBookClick,
            modifier = Modifier.weight(1f)
        ) {
            Text("Book Now")
        }
        Spacer(modifier = Modifier.width(8.dp))
        OutlinedButton(
            onClick = onSeeMoreClick,
            modifier = Modifier.weight(1f)
        ) {
            Text("See More")
        }
    }
}