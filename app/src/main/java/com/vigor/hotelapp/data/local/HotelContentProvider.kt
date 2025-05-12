package com.vigor.hotelapp.data.local

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.room.Room
import java.io.File

class HotelContentProvider : ContentProvider() {

    private lateinit var database: AppDatabase

    override fun onCreate(): Boolean {
        context?.let {
            database = Room.databaseBuilder(
                it.applicationContext,
                AppDatabase::class.java,
                "hotel_database"
            ).build()
        }
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        // Implement if needed for querying hotels/bookings
        return null
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        return 0
    }

    override fun getStreamTypes(uri: Uri, mimeTypeFilter: String): Array<String>? {
        return arrayOf("application/octet-stream")
    }

    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
        val dbFile = context?.getDatabasePath("hotel_database")
        return ParcelFileDescriptor.open(dbFile, ParcelFileDescriptor.MODE_READ_WRITE)
    }
}