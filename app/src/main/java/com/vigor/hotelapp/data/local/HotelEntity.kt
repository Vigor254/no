package com.vigor.hotelapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hotels")
data class HotelEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val imageResId: Int,
    val pricePerHour: Double,
    val location: String
)

fun HotelEntity.toHotel(): com.vigor.hotelapp.model.Hotel {
    return com.vigor.hotelapp.model.Hotel(
        id = id,
        name = name,
        description = description,
        imageResId = imageResId,
        pricePerHour = pricePerHour,
        location = location
    )
}

fun com.vigor.hotelapp.model.Hotel.toHotelEntity(): HotelEntity {
    return HotelEntity(
        id = id,
        name = name,
        description = description,
        imageResId = imageResId,
        pricePerHour = pricePerHour,
        location = location
    )
}