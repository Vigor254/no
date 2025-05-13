package com.vigor.hotelapp.data

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import com.vigor.hotelapp.data.local.AppDatabase
import com.vigor.hotelapp.model.Hotel
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class HotelProvider : ContentProvider() {

    companion object {
        private const val AUTHORITY = "com.vigor.hotelapp.provider"
        private const val HOTELS = 1
        private const val HOTEL_ID = 2
        private val URI_MATCHER = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "hotels", HOTELS)
            addURI(AUTHORITY, "hotels/#", HOTEL_ID)
        }
    }

    @Inject
    lateinit var hotelRepository: HotelRepository

    override fun onCreate(): Boolean {
        val hiltEntryPoint = EntryPointAccessors.fromApplication(
            context!!,
            AppDatabase.HotelRepositoryEntryPoint::class.java
        )
        hotelRepository = hiltEntryPoint.hotelRepository()
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        return when (URI_MATCHER.match(uri)) {
            HOTELS -> {
                val hotels = runBlocking { hotelRepository.getAllHotelsSync() }
                val cursor = MatrixCursor(arrayOf("id", "name", "description", "imageResId", "pricePerHour", "location"))
                hotels.forEach { hotel ->
                    cursor.addRow(arrayOf(hotel.id, hotel.name, hotel.description, hotel.imageResId, hotel.pricePerHour, hotel.location))
                }
                cursor
            }
            HOTEL_ID -> {
                val id = uri.lastPathSegment?.toIntOrNull() ?: return null
                val hotel = runBlocking { hotelRepository.getHotelById(id) }
                val cursor = MatrixCursor(arrayOf("id", "name", "description", "imageResId", "pricePerHour", "location"))
                hotel?.let {
                    cursor.addRow(arrayOf(it.id, it.name, it.description, it.imageResId, it.pricePerHour, it.location))
                }
                cursor
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return when (URI_MATCHER.match(uri)) {
            HOTELS -> {
                val hotel = values?.let { createHotelFromValues(it) } ?: return null
                val id = runBlocking { hotelRepository.getAllHotelsSync().maxOfOrNull { it.id }?.plus(1) ?: 1 }
                val newHotel = hotel.copy(id = id)
                runBlocking { hotelRepository.insertHotel(newHotel) }
                Uri.withAppendedPath(uri, id.toString())
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        return when (URI_MATCHER.match(uri)) {
            HOTEL_ID -> {
                val id = uri.lastPathSegment?.toIntOrNull() ?: return 0
                val hotel = values?.let { createHotelFromValues(it) }?.copy(id = id) ?: return 0
                runBlocking {
                    hotelRepository.updateHotel(hotel)
                    1
                }
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return when (URI_MATCHER.match(uri)) {
            HOTEL_ID -> {
                val id = uri.lastPathSegment?.toIntOrNull() ?: return 0
                runBlocking {
                    hotelRepository.deleteHotel(id)
                    1
                }
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun getType(uri: Uri): String? {
        return when (URI_MATCHER.match(uri)) {
            HOTELS -> "vnd.android.cursor.dir/vnd.$AUTHORITY.hotels"
            HOTEL_ID -> "vnd.android.cursor.item/vnd.$AUTHORITY.hotels"
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    private fun createHotelFromValues(values: ContentValues): Hotel {
        return Hotel(
            id = values.getAsInteger("id") ?: 0,
            name = values.getAsString("name") ?: "",
            description = values.getAsString("description") ?: "",
            imageResId = values.getAsInteger("imageResId") ?: 0,
            pricePerHour = values.getAsDouble("pricePerHour") ?: 0.0,
            location = values.getAsString("location") ?: ""
        )
    }
}