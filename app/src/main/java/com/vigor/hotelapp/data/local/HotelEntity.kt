package com.vigor.hotelapp.data.local

import android.R.attr.description
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vigor.hotelapp.model.Hotel

@Entity(tableName = "hotels")
data class HotelEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String,
    val imageResId: Int,
    val pricePerHour: Double,
    val location: String
)

fun HotelEntity.toHotel(): Hotel {
    return Hotel(
        id = id,
        name = name,
        description = description,
        imageResId = imageResId,
        pricePerHour = pricePerHour,
        location = location
    )
}

fun Hotel.toHotelEntity(): HotelEntity {
    return HotelEntity(
        id = id,
        name = name,
        description = description,
        imageResId = imageResId,
        pricePerHour = pricePerHour,
        location = location
    )
}