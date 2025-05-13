package com.vigor.hotelapp.data

import com.vigor.hotelapp.data.local.HotelDao
import com.vigor.hotelapp.data.local.toHotel
import com.vigor.hotelapp.data.local.toHotelEntity
import com.vigor.hotelapp.model.Booking
import com.vigor.hotelapp.model.Hotel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HotelRepository @Inject constructor(
    private val hotelDao: HotelDao
) {
    fun getAllHotels(): Flow<List<Hotel>> {
        return hotelDao.getAllHotels().map { entities -> entities.map { it.toHotel() } }
    }

    suspend fun getAllHotelsSync(): List<Hotel> {
        return hotelDao.getAllHotels().first().map { it.toHotel() }
    }

    suspend fun getHotelById(id: Int): Hotel? {
        return hotelDao.getHotelById(id)?.toHotel()
    }

    suspend fun insertHotel(hotel: Hotel) {
        hotelDao.insertHotel(hotel.toHotelEntity())
    }

    suspend fun updateHotel(hotel: Hotel) {
        hotelDao.updateHotel(hotel.toHotelEntity())
    }

    suspend fun deleteHotel(id: Int) {
        hotelDao.deleteHotel(id)
    }

    suspend fun insertBooking(booking: Booking) {
        hotelDao.insertBooking(booking)
    }

    suspend fun getBookingsByUser(userId: String): List<Booking> {
        return hotelDao.getBookingsByUser(userId)
    }
}