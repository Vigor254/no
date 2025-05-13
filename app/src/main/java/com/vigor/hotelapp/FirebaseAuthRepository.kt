package com.vigor.hotelapp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    suspend fun isAdmin(userId: String): Boolean {
        val doc = firestore.collection("users").document(userId).get().await()
        return doc.getBoolean("isAdmin") ?: false
    }

    fun getCurrentUserId(): String? = auth.currentUser?.uid
}