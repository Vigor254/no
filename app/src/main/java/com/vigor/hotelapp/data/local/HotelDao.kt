package com.vigor.hotelapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.vigor.hotelapp.model.Booking
import kotlinx.coroutines.flow.Flow

@Dao
interface HotelDao {
    @Query("SELECT * FROM hotels")
    fun getAllHotels(): Flow<List<HotelEntity>>

    @Query("SELECT * FROM hotels WHERE id = :id")
    suspend fun getHotelById(id: Int): HotelEntity?

    @Insert
    suspend fun insertHotel(hotel: HotelEntity)

    @Insert
    fun insertHotelSync(hotel: HotelEntity): Long

    @Update
    suspend fun updateHotel(hotel: HotelEntity)

    @Update
    fun updateHotelSync(hotel: HotelEntity): Int

    @Query("DELETE FROM hotels WHERE id = :id")
    suspend fun deleteHotel(id: Int)

    @Query("DELETE FROM hotels WHERE id = :id")
    fun deleteHotelSync(id: Int): Int

    @Insert
    suspend fun insertBooking(booking: Booking)

    @Query("SELECT * FROM bookings WHERE userId = :userId")
    suspend fun getBookingsByUser(userId: String): List<Booking>
}