package com.example.fullcrossapplication.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fullcrossapplication.data.StreamSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AdminViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _streamSettings = MutableStateFlow<StreamSettings?>(null)
    val streamSettings: StateFlow<StreamSettings?> = _streamSettings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadStreamSettings() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val snapshot = firestore.collection("settings")
                    .document("stream")
                    .get()
                    .await()

                _streamSettings.value = snapshot.toObject(StreamSettings::class.java)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to load stream settings: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateStreamUrl(newUrl: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val currentUser = auth.currentUser
                    ?: throw Exception("No user logged in")

                val settings = StreamSettings(
                    streamUrl = newUrl,
                    lastUpdated = System.currentTimeMillis(),
                    updatedBy = currentUser.uid
                )

                firestore.collection("settings")
                    .document("stream")
                    .set(settings)
                    .await()

                _streamSettings.value = settings
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to update stream URL: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
} 