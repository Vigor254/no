package com.vigor.hotelapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vigor.hotelapp.data.HotelRepository
import com.vigor.hotelapp.model.Booking
import com.vigor.hotelapp.model.Hotel
import com.vigor.hotelapp.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HotelViewModel @Inject constructor(
    private val hotelRepository: HotelRepository,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {
    private val _hotels = MutableStateFlow<List<Hotel>>(emptyList())
    val hotels: StateFlow<List<Hotel>> = _hotels.asStateFlow()

    private val _bookings = MutableStateFlow<List<Booking>>(emptyList())
    val bookings: StateFlow<List<Booking>> = _bookings.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    init {
        loadHotels()
        loadUser()
    }

    private fun loadHotels() {
        viewModelScope.launch {
            hotelRepository.getAllHotels().collect { hotels ->
                Log.d("HotelViewModel", "Loaded ${hotels.size} hotels: $hotels")
                _hotels.value = hotels
            }
        }
    }

    private fun loadUser() {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                try {
                    val doc = firestore.collection("users").document(userId).get().await()
                    if (doc.exists()) {
                        val fullName = doc.getString("fullName") ?: ""
                        val email = doc.getString("email") ?: ""
                        _currentUser.value = User(userId, fullName, email)
                        _isAdmin.value = doc.getBoolean("isAdmin") ?: false
                        _bookings.value = hotelRepository.getBookingsByUser(userId)
                    }
                } catch (e: Exception) {
                    _authError.value = "Failed to load user: ${e.message}"
                }
            }
        }
    }

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                loadUser()
                _authError.value = null
                onResult(true)
            } catch (e: Exception) {
                _authError.value = "Login failed: ${e.message}"
                onResult(false)
            }
        }
    }

    fun signup(fullName: String, email: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val userId = result.user?.uid
                if (userId != null) {
                    val userData = hashMapOf(
                        "fullName" to fullName,
                        "email" to email,
                        "isAdmin" to false
                    )
                    firestore.collection("users").document(userId).set(userData).await()
                    loadUser()
                    _authError.value = null
                    onResult(true)
                } else {
                    _authError.value = "Signup failed: No user ID"
                    onResult(false)
                }
            } catch (e: Exception) {
                _authError.value = "Signup failed: ${e.message}"
                onResult(false)
            }
        }
    }

    fun logout() {
        auth.signOut()
        _currentUser.value = null
        _isAdmin.value = false
        _bookings.value = emptyList()
        _authError.value = null
    }

    fun bookHotel(hotelId: Int, hours: Int, phoneNumber: String, notes: String) {
        viewModelScope.launch {
            val hotel = hotelRepository.getHotelById(hotelId)
            val userId = auth.currentUser?.uid
            if (hotel != null && userId != null) {
                val totalCost = hotel.pricePerHour * hours
                val bookingDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val booking = Booking(
                    hotelId = hotelId,
                    userId = userId,
                    hours = hours,
                    totalCost = totalCost,
                    bookingDate = bookingDate,
                    phoneNumber = phoneNumber,
                    notes = notes
                )
                hotelRepository.insertBooking(booking)
                _bookings.value = hotelRepository.getBookingsByUser(userId)
            }
        }
    }

    fun addHotel(name: String, description: String, pricePerHour: Double, imageResId: Int, location: String) {
        viewModelScope.launch {
            val hotel = Hotel(
                name = name,
                description = description,
                pricePerHour = pricePerHour,
                imageResId = imageResId,
                location = location
            )
            hotelRepository.insertHotel(hotel)
        }
    }

    fun updateHotel(id: Int, name: String, description: String, pricePerHour: Double, imageResId: Int, location: String) {
        viewModelScope.launch {
            val hotel = Hotel(
                id = id,
                name = name,
                description = description,
                pricePerHour = pricePerHour,
                imageResId = imageResId,
                location = location
            )
            hotelRepository.updateHotel(hotel)
        }
    }

    fun deleteHotel(id: Int) {
        viewModelScope.launch {
            hotelRepository.deleteHotel(id)
        }
    }
}