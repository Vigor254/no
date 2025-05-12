package com.vigor.hotelapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.vigor.hotelapp.model.Hotel
import com.vigor.hotelapp.model.User
import com.vigor.hotelapp.viewmodel.HotelViewModel

@ExperimentalMaterial3Api
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HotelViewModel = hiltViewModel<HotelViewModel>()
) {
    val hotels = viewModel.hotels.value
    val user = viewModel.currentUser.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("HotelApp") },
                actions = {
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
                .padding(horizontal = 16.dp)
        ) {
            if (hotels.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn {
                    items(hotels) { hotel ->
                        HotelCard(hotel = hotel, onClick = {
                            navController.navigate("hotelDetails?hotelId=${hotel.id}")
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun HotelCard(hotel: Hotel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = hotel.imageResId,
                contentDescription = hotel.name,
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 16.dp)
            )
            Column {
                Text(
                    text = hotel.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = hotel.location,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "$${hotel.pricePerHour}/hour",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}