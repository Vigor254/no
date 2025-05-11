package com.vigor.hotelapp.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.vigor.hotelapp.model.Booking

@Database(entities = [HotelEntity::class, Booking::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun hotelDao(): HotelDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Migration to handle schema change from imageUrl to imageResId
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create a new table with the updated schema (imageResId instead of imageUrl)
                database.execSQL(
                    """
                    CREATE TABLE hotels_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        description TEXT NOT NULL,
                        imageResId INTEGER NOT NULL,
                        pricePerHour REAL NOT NULL,
                        location TEXT NOT NULL
                    )
                    """.trimIndent()
                )
                // Copy data from the old table, using a default value (0) for imageResId
                database.execSQL(
                    """
                    INSERT INTO hotels_new (id, name, description, imageResId, pricePerHour, location)
                    SELECT id, name, description, 0, pricePerHour, location
                    FROM hotels
                    """.trimIndent()
                )
                // Drop the old table and rename the new one
                database.execSQL("DROP TABLE hotels")
                database.execSQL("ALTER TABLE hotels_new RENAME TO hotels")
            }
        }

        fun getDatabase(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hotel_database"
                )
                    .addMigrations(MIGRATION_1_2) // Add migration for schema change
                    .fallbackToDestructiveMigration() // Fallback for other changes
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}