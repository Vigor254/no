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
fun LoginScreen(navController: NavHostController, viewModel: HotelViewModel = hiltViewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authError by viewModel.authError

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Login", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.login(email, password) { success ->
                    if (success) {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = { navController.navigate("signup") }
        ) {
            Text("Don't have an account? Sign Up")
        }

        authError?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.weight(1f))

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