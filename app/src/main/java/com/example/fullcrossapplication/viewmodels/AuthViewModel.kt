package com.example.fullcrossapplication.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fullcrossapplication.repository.FirebaseRepository
import com.example.fullcrossapplication.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import android.net.Uri

class AuthViewModel(
    private val repository: FirebaseRepository = FirebaseRepository()
) : ViewModel() {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _friendsCount = MutableStateFlow(0)
    val friendsCount: StateFlow<Int> = _friendsCount

    private val _profileImageUri = MutableStateFlow<Uri?>(null)
    val profileImageUri = _profileImageUri.asStateFlow()

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        viewModelScope.launch {
            _currentUser.value = repository.getCurrentUser()
        }
    }

    fun signUp(firstName: String, lastName: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            repository.signUp(firstName, lastName, email, password)
                .onSuccess { user ->
                    _currentUser.value = user
                }
                .onFailure { exception ->
                    _error.value = exception.message
                }
            
            _isLoading.value = false
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            repository.signIn(email, password)
                .onSuccess { user ->
                    _currentUser.value = user
                }
                .onFailure { exception ->
                    _error.value = "Invalid email or password. Please try again."
                }
            
            _isLoading.value = false
        }
    }

    fun signOut() {
        repository.signOut()
        _currentUser.value = null
    }

    fun clearError() {
        _error.value = null
    }

    fun changePassword(currentPassword: String, newPassword: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            repository.changePassword(currentPassword, newPassword)
                .onSuccess {
                    onComplete(true)
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Failed to change password"
                    onComplete(false)
                }
            
            _isLoading.value = false
        }
    }

    fun setError(message: String) {
        _error.value = message
    }

    fun updateProfile(
        firstName: String,
        lastName: String,
        phoneNumber: String,
        email: String,
        onComplete: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            repository.updateUserProfile(firstName, lastName, phoneNumber)
                .onSuccess {
                    _currentUser.value = repository.getCurrentUser()
                    onComplete(true)
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Failed to update profile"
                    onComplete(false)
                }
            
            _isLoading.value = false
        }
    }

    fun fetchFriendsCount() {
        viewModelScope.launch {
            try {
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                
                // Query friendships collection and count only accepted friendships
                val snapshot = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(currentUserId)
                    .collection("friendships")
                    .whereEqualTo("status", "accepted")
                    .get()
                    .await()
                
                _friendsCount.value = snapshot.size()
            } catch (e: Exception) {
                // Handle error
                _friendsCount.value = 0
            }
        }
    }

    fun updateProfileImage(uri: Uri) {
        viewModelScope.launch {
            try {
                // TODO: Upload image to your backend/storage
                _profileImageUri.value = uri
            } catch (e: Exception) {
                _error.value = "Failed to update profile image: ${e.message}"
            }
        }
    }
} 