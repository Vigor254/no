package com.vigor.hotelapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vigor.hotelapp.data.HotelRepository
import com.vigor.hotelapp.model.Booking
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Database(entities = [HotelEntity::class, Booking::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun hotelDao(): HotelDao

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface HotelRepositoryEntryPoint {
        fun hotelRepository(): HotelRepository
    }
}