package com.vigor.hotelapp

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HotelApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
    }
}