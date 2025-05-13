package com.vigor.hotelapp.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vigor.hotelapp.R
import com.vigor.hotelapp.viewmodel.HotelViewModel
import kotlin.collections.find

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditHotelScreen(
    viewModel: HotelViewModel = hiltViewModel(),
    hotelId: Int,
    onSave: () -> Unit
) {
    val hotels = viewModel.hotels.collectAsState().value
    val hotel = hotels.find { it.id == hotelId }
    var name by remember { mutableStateOf(hotel?.name ?: "") }
    var description by remember { mutableStateOf(hotel?.description ?: "") }
    var location by remember { mutableStateOf(hotel?.location ?: "") }
    var pricePerHour by remember { mutableStateOf(hotel?.pricePerHour?.toString() ?: "") }
    val imageResId = hotel?.imageResId ?: R.drawable.hotel1

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (hotelId == -1) "Add Hotel" else "Edit Hotel") },
                navigationIcon = {
                    IconButton(onClick = onSave) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = pricePerHour,
                onValueChange = { pricePerHour = it },
                label = { Text("Price per Hour") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    val price = pricePerHour.toDoubleOrNull() ?: 0.0
                    if (hotelId == -1) {
                        viewModel.addHotel(name, description, price, imageResId, location)
                    } else {
                        viewModel.updateHotel(hotelId, name, description, price, imageResId, location)
                    }
                    onSave()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
        }
    }
}