package com.vigor.hotelapp.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.vigor.hotelapp.R
import com.vigor.hotelapp.data.HotelRepository
import com.vigor.hotelapp.model.Booking
import com.vigor.hotelapp.model.Hotel
import com.vigor.hotelapp.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HotelViewModel @Inject constructor(
    private val repository: HotelRepository
) : ViewModel() {

    val hotels = mutableStateOf<List<Hotel>>(emptyList())
    val currentUser = mutableStateOf<User?>(null)
    val bookings = mutableStateOf<List<Booking>>(emptyList())
    val authError = mutableStateOf<String?>(null) // To display errors to UI

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    init {
        // Check if user is already signed in
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            currentUser.value = User(
                id = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                isAdmin = firebaseUser.email == "admin@hotelapp.com"
            )
            loadBookings()
        }

        // Load initial hotels
        viewModelScope.launch {
            val currentHotels = repository.getAllHotels()
            if (currentHotels.isEmpty()) {
                val initialHotels = listOf(
                    Hotel(
                        name = "Grand Hotel",
                        description = "A luxurious hotel with stunning views.",
                        imageResId = R.drawable.hotel1,
                        pricePerHour = 50.0,
                        location = "Downtown"
                    ),
                    Hotel(
                        name = "Ocean Breeze",
                        description = "A beachfront hotel with relaxing vibes.",
                        imageResId = R.drawable.hotel2,
                        pricePerHour = 40.0,
                        location = "Beachside"
                    ),
                    Hotel(
                        name = "Mountain Retreat",
                        description = "A cozy retreat in the mountains.",
                        imageResId = R.drawable.hotel3,
                        pricePerHour = 60.0,
                        location = "Mountains"
                    )
                )
                initialHotels.forEach { repository.insertHotel(it) }
            }
            loadHotels()
        }
    }

    fun loadHotels() {
        viewModelScope.launch {
            val loadedHotels = repository.getAllHotels().map { hotel ->
                if (hotel.imageResId == 0) {
                    hotel.copy(imageResId = R.drawable.hotel1)
                } else {
                    hotel
                }
            }
            hotels.value = loadedHotels
        }
    }

    fun addHotel(hotel: Hotel) {
        viewModelScope.launch {
            val hotelWithValidImage = if (hotel.imageResId == 0) {
                hotel.copy(imageResId = R.drawable.hotel1)
            } else {
                hotel
            }
            repository.insertHotel(hotelWithValidImage)
            loadHotels()
        }
    }

    fun deleteHotel(hotelId: Int) {
        viewModelScope.launch {
            repository.deleteHotel(hotelId)
            loadHotels()
        }
    }

    fun bookHotel(hotelId: Int, hours: Int) {
        viewModelScope.launch {
            val hotel = repository.getHotelById(hotelId) ?: return@launch
            val totalCost = hotel.pricePerHour * hours
            val userId = currentUser.value?.id ?: return@launch
            val booking = Booking(
                userId = userId,
                hotelId = hotelId,
                hours = hours,
                totalCost = totalCost,
                bookingDate = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
            )
            repository.insertBooking(booking)
            loadBookings()
        }
    }

    fun loadBookings() {
        viewModelScope.launch {
            val userId = currentUser.value?.id ?: return@launch
            bookings.value = repository.getBookingsByUser(userId)
        }
    }

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener { result ->
                        val firebaseUser = result.user
                        if (firebaseUser != null) {
                            currentUser.value = User(
                                id = firebaseUser.uid,
                                email = firebaseUser.email ?: "",
                                isAdmin = email == "admin@hotelapp.com" // TODO: Use custom claims for admin
                            )
                            loadBookings()
                            authError.value = null
                            onResult(true)
                        } else {
                            authError.value = "Login failed: No user found"
                            onResult(false)
                        }
                    }
                    .addOnFailureListener { exception ->
                        authError.value = when (exception) {
                            is FirebaseAuthInvalidUserException -> "No account exists for this email"
                            is FirebaseAuthInvalidCredentialsException -> "Invalid password"
                            else -> "Login failed: ${exception.message}"
                        }
                        onResult(false)
                    }
            } catch (e: Exception) {
                authError.value = "Login error: ${e.message}"
                onResult(false)
            }
        }
    }

    fun signup(email: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener { result ->
                        val firebaseUser = result.user
                        if (firebaseUser != null) {
                            currentUser.value = User(
                                id = firebaseUser.uid,
                                email = firebaseUser.email ?: "",
                                isAdmin = email == "admin@hotelapp.com"
                            )
                            authError.value = null
                            onResult(true)
                        } else {
                            authError.value = "Signup failed: No user created"
                            onResult(false)
                        }
                    }
                    .addOnFailureListener { exception ->
                        authError.value = when (exception) {
                            is FirebaseAuthUserCollisionException -> "Email already in use"
                            else -> "Signup failed: ${exception.message}"
                        }
                        onResult(false)
                    }
            } catch (e: Exception) {
                authError.value = "Signup error: ${e.message}"
                onResult(false)
            }
        }
    }

    fun logout() {
        firebaseAuth.signOut()
        currentUser.value = null
        bookings.value = emptyList()
        authError.value = null
    }
}