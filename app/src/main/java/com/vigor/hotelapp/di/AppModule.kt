package com.vigor.hotelapp.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vigor.hotelapp.R
import com.vigor.hotelapp.data.HotelRepository
import com.vigor.hotelapp.data.local.AppDatabase
import com.vigor.hotelapp.data.local.HotelDao
import com.vigor.hotelapp.data.local.HotelEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("""
                CREATE TABLE Booking_new (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    hotelId INTEGER NOT NULL,
                    userId TEXT NOT NULL,
                    hours INTEGER NOT NULL,
                    totalCost REAL NOT NULL,
                    bookingDate TEXT NOT NULL,
                    phoneNumber TEXT NOT NULL,
                    notes TEXT NOT NULL
                )
            """)
            database.execSQL("""
                INSERT INTO Booking_new (id, hotelId, userId, hours, totalCost, bookingDate, phoneNumber, notes)
                SELECT id, hotelId, userId, hours, 0.0, '', '', ''
                FROM Booking
            """)
            database.execSQL("DROP TABLE Booking")
            database.execSQL("ALTER TABLE Booking_new RENAME TO Booking")
        }
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "hotel_database"
        )
            .addMigrations(MIGRATION_1_2)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // Seed initial hotels
                    CoroutineScope(Dispatchers.IO).launch {
                        val database = Room.databaseBuilder(
                            context,
                            AppDatabase::class.java,
                            "hotel_database"
                        ).build()
                        val dao = database.hotelDao()
                        dao.insertHotel(
                            HotelEntity(
                                name = "City Center Hotel",
                                description = "A cozy hotel in the heart of the city",
                                imageResId = R.drawable.hotel1,
                                pricePerHour = 50.0,
                                location = "Downtown"
                            )
                        )
                        dao.insertHotel(
                            HotelEntity(
                                name = "Luxury Sky Resort",
                                description = "Luxury stay with a stunning view",
                                imageResId = R.drawable.hotel1,
                                pricePerHour = 80.0,
                                location = "Uptown"
                            )
                        )
                        dao.insertHotel(
                            HotelEntity(
                                name = "Beachfront Inn",
                                description = "Relax by the sea",
                                imageResId = R.drawable.hotel1,
                                pricePerHour = 65.0,
                                location = "Coast"
                            )
                        )
                        database.close()
                    }
                }
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideHotelDao(database: AppDatabase): HotelDao {
        return database.hotelDao()
    }

    @Provides
    @Singleton
    fun provideHotelRepository(hotelDao: HotelDao): HotelRepository {
        return HotelRepository(hotelDao)
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }
}