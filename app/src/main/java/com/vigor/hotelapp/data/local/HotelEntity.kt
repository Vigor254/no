package com.vigor.hotelapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vigor.hotelapp.model.Hotel

@Entity(tableName = "hotels")
data class HotelEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val imageResId: Int, // Changed from imageUrl: String to imageResId: Int
    val pricePerHour: Double,
    val location: String
) {
    // Convert HotelEntity to Hotel (used when reading from database)
    fun toHotel(): Hotel {
        return Hotel(
            id = id,
            name = name,
            description = description,
            imageResId = imageResId, // Updated to use imageResId
            pricePerHour = pricePerHour,
            location = location
        )
    }
}

// Extension function to convert Hotel to HotelEntity (used when writing to database)
fun Hotel.toHotelEntity(): HotelEntity {
    return HotelEntity(
        id = id,
        name = name,
        description = description,
        imageResId = imageResId, // Updated to use imageResId
        pricePerHour = pricePerHour,
        location = location
    )
}