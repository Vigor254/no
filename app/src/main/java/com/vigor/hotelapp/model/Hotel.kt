package com.vigor.hotelapp.model

data class Hotel(
    val id: Int = 0,
    val name: String,
    val description: String,
    val imageResId: Int, // Changed from imageUrl: String to imageResId: Int
    val pricePerHour: Double,
    val location: String
)