package com.vigor.hotelapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.vigor.hotelapp.navigation.NavGraph
import com.vigor.hotelapp.ui.theme.HotelAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HotelAppTheme {
                HotelApp()
            }
        }
    }
}

@Composable
fun HotelApp() {
    val navController = rememberNavController()
    NavGraph(navController = navController)
}