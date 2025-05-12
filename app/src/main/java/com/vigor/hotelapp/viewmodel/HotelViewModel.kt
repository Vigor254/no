package com.vigor.hotelapp.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
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
    val authError = mutableStateOf<String?>(null)

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    init {
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

            auth.currentUser?.let { firebaseUser ->
                currentUser.value = User(
                    id = firebaseUser.uid,
                    fullName = firebaseUser.displayName ?: "Guest",
                    email = firebaseUser.email ?: ""
                )
                loadBookings()
            }
        }
    }

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val firebaseUser = auth.currentUser
                            currentUser.value = firebaseUser?.let {
                                User(
                                    id = it.uid,
                                    fullName = it.displayName ?: "Guest",
                                    email = it.email ?: ""
                                )
                            }
                            loadBookings()
                            authError.value = null
                            onResult(true)
                            Log.d("HotelViewModel", "Logged in as: $email")
                        } else {
                            handleAuthError(task.exception)
                            onResult(false)
                        }
                    }
            } catch (e: Exception) {
                handleAuthError(e)
                onResult(false)
            }
        }
    }

    fun signup(fullName: String, email: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val firebaseUser = auth.currentUser
                            firebaseUser?.updateProfile(
                                com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                    .setDisplayName(fullName)
                                    .build()
                            )
                            currentUser.value = firebaseUser?.let {
                                User(
                                    id = it.uid,
                                    fullName = fullName,
                                    email = it.email ?: ""
                                )
                            }
                            authError.value = null
                            onResult(true)
                            Log.d("HotelViewModel", "Signed up as: $email")
                        } else {
                            handleAuthError(task.exception)
                            onResult(false)
                        }
                    }
            } catch (e: Exception) {
                handleAuthError(e)
                onResult(false)
            }
        }
    }

    fun logout() {
        auth.signOut()
        currentUser.value = null
        bookings.value = emptyList()
        authError.value = null
        Log.d("HotelViewModel", "Logged out")
    }

    private fun handleAuthError(exception: Exception?) {
        authError.value = when (exception) {
            is FirebaseAuthException -> {
                when (exception.errorCode) {
                    "ERROR_INVALID_EMAIL" -> "Invalid email format."
                    "ERROR_WRONG_PASSWORD" -> "Incorrect password."
                    "ERROR_USER_NOT_FOUND" -> "No account exists for this email."
                    "ERROR_EMAIL_ALREADY_IN_USE" -> "This email is already registered."
                    "ERROR_WEAK_PASSWORD" -> "Password is too weak. Use at least 6 characters."
                    else -> "Authentication failed: ${exception.message}"
                }
            }
            else -> "An error occurred: ${exception?.message}"
        }
        Log.e("HotelViewModel", "Auth error: ${exception?.message}")
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

    fun bookHotel(hotelId: Int, hours: Int) {
        viewModelScope.launch {
            val hotel = repository.getHotelById(hotelId) ?: return@launch
            val totalCost = hotel.pricePerHour * hours
            val booking = Booking(
                userId = currentUser.value?.id ?: return@launch,
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
}