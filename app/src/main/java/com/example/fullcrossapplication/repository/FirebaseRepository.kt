package com.example.fullcrossapplication.repository

import com.example.fullcrossapplication.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class FirebaseRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    suspend fun signUp(
        email: String, 
        password: String, 
        firstName: String, 
        lastName: String,
        phoneNumber: String = "",
        roles: List<String> = emptyList()
    ): Result<User> {
        return try {
            println("Email passed to Firebase: '${email.trim()}'")
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("Failed to get user ID")
            
            val user = User(
                uid = uid,
                email = email,
                firstName = firstName,
                lastName = lastName,
                phoneNumber = phoneNumber,
                roles = roles
            )
            
            try {
                usersCollection.document(uid).set(user).await()
                Result.success(user)
            } catch (e: Exception) {
                auth.currentUser?.delete()?.await()
                throw Exception("Failed to create user profile: ${e.message}")
            }
        } catch (e: Exception) {
            Result.failure(Exception("Sign up failed: ${e.message}"))
        }
    }

    suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("Failed to get user ID")
            
            val userDoc = usersCollection.document(uid).get().await()
            val user = userDoc.toObject(User::class.java)
                ?: throw Exception("Failed to get user profile")
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(Exception("Sign in failed: ${e.message}"))
        }
    }

    suspend fun getCurrentUser(): User? {
        val uid = auth.currentUser?.uid ?: return null
        return try {
            val userDoc = usersCollection.document(uid).get().await()
            userDoc.toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun signOut() {
        auth.signOut()
    }

    suspend fun updateUserProfile(
        firstName: String? = null,
        lastName: String? = null,
        phoneNumber: String? = null
    ): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
        
        return try {
            val updates = mutableMapOf<String, Any>()
            firstName?.let { updates["firstName"] = it }
            lastName?.let { updates["lastName"] = it }
            phoneNumber?.let { updates["phoneNumber"] = it }
            
            usersCollection.document(uid).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> {
        val user = auth.currentUser ?: return Result.failure(Exception("User not logged in"))
        val email = user.email ?: return Result.failure(Exception("User email not found"))
        
        return try {
            // Re-authenticate user with current password
            val credential = EmailAuthProvider.getCredential(email, currentPassword)
            user.reauthenticate(credential).await()
            
            // Update password
            user.updatePassword(newPassword).await()
            Result.success(Unit)
        } catch (e: Exception) {
            when {
                e is FirebaseAuthInvalidCredentialsException -> 
                    Result.failure(Exception("Current password is incorrect"))
                e is FirebaseAuthWeakPasswordException -> 
                    Result.failure(Exception("New password is too weak. Please use at least 6 characters"))
                else -> Result.failure(Exception("Failed to change password. Please try again later"))
            }
        }
    }
} 