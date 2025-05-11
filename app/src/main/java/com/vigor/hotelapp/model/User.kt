package com.vigor.hotelapp.model

data class User(
    val id: String, // Firebase UID
    val email: String,
    val isAdmin: Boolean = false
)