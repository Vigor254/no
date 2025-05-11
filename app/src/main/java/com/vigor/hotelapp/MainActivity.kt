package com.vigor.hotelapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import com.vigor.hotelapp.ui.theme.HotelAppTheme
import com.vigor.hotelapp.navigation.AppNavHost

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HotelAppTheme {
                val navController = rememberNavController()
                AppNavHost(navController = navController)
            }
        }
    }
}