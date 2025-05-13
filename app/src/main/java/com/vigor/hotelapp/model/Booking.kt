package com.vigor.hotelapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class Booking(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val hotelId: Int,
    val userId: String,
    val hours: Int,
    val totalCost: Double,
    val bookingDate: String,
    val phoneNumber: String,
    val notes: String
)