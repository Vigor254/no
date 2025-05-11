package com.vigor.hotelapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.vigor.hotelapp.model.Booking

@Dao
interface HotelDao {
    @Insert
    suspend fun insertHotel(hotel: HotelEntity)

    @Query("SELECT * FROM hotels")
    suspend fun getAllHotels(): List<HotelEntity>

    @Query("SELECT * FROM hotels WHERE id = :id")
    suspend fun getHotelById(id: Int): HotelEntity?

    @Query("DELETE FROM hotels WHERE id = :id")
    suspend fun deleteHotel(id: Int)

    @Insert
    suspend fun insertBooking(booking: Booking)

    @Query("SELECT * FROM bookings WHERE userId = :userId")
    suspend fun getBookingsByUser(userId: String): List<Booking>
}