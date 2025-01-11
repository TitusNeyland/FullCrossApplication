package com.example.fullcrossapplication.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fullcrossapplication.data.Contact
import com.example.fullcrossapplication.repository.ContactsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.firestore.FirebaseFirestore
import com.example.fullcrossapplication.data.UserProfile
import kotlinx.coroutines.tasks.await
import com.google.firebase.auth.FirebaseAuth

class ContactsViewModel(application: Application) : AndroidViewModel(application) {
    private val contactsRepository = ContactsRepository(application)
    
    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> = _contacts
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery
    
    private val _searchResults = MutableStateFlow<List<UserProfile>>(emptyList())
    val searchResults: StateFlow<List<UserProfile>> = _searchResults
    
    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching
    
    fun syncContacts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val contactsList = contactsRepository.getContacts()
                _contacts.value = contactsList
            } catch (e: Exception) {
                _error.value = "Failed to sync contacts: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun searchUsers(query: String) {
        viewModelScope.launch {
            _searchQuery.value = query
            _isSearching.value = true
            
            try {
                if (query.length >= 2) {
                    val cleanQuery = query.trim().lowercase()
                    FirebaseFirestore.getInstance()
                        .collection("users")
                        .get()
                        .await()
                        .documents
                        .mapNotNull { doc ->
                            val firstName = doc.getString("firstName")?.lowercase() ?: ""
                            val lastName = doc.getString("lastName")?.lowercase() ?: ""
                            val phone = doc.getString("phoneNumber")?.replace("[^0-9]".toRegex(), "") ?: ""
                            
                            if (firstName.contains(cleanQuery) || 
                                lastName.contains(cleanQuery) || 
                                phone.contains(cleanQuery)
                            ) {
                                UserProfile(
                                    id = doc.id,
                                    firstName = doc.getString("firstName") ?: "",
                                    lastName = doc.getString("lastName") ?: "",
                                    phoneNumber = phone
                                )
                            } else null
                        }
                        .also { _searchResults.value = it }
                } else {
                    _searchResults.value = emptyList()
                }
            } catch (e: Exception) {
                _error.value = "Search failed: ${e.message}"
                _searchResults.value = emptyList()
            } finally {
                _isSearching.value = false
            }
        }
    }
    
    fun clearSearch() {
        _searchQuery.value = ""
        _searchResults.value = emptyList()
    }
    
    fun sendFriendRequest(toUserId: String, toUserName: String) {
        viewModelScope.launch {
            try {
                val currentUser = FirebaseAuth.getInstance().currentUser
                    ?: throw Exception("Not logged in")
                
                val currentUserDoc = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(currentUser.uid)
                    .get()
                    .await()
                
                val currentUserName = "${currentUserDoc.getString("firstName")} ${currentUserDoc.getString("lastName")}"
                
                // Create notification document
                val notification = hashMapOf(
                    "type" to "FRIEND_REQUEST",
                    "fromUserId" to currentUser.uid,
                    "fromUserName" to currentUserName,
                    "timestamp" to System.currentTimeMillis(),
                    "read" to false
                )
                
                // Add notification to recipient's notifications collection
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(toUserId)
                    .collection("notifications")
                    .add(notification)
                    .await()
                
            } catch (e: Exception) {
                _error.value = "Failed to send friend request: ${e.message}"
            }
        }
    }
    
    fun acceptFriendRequest(fromUserId: String) {
        viewModelScope.launch {
            try {
                val currentUser = FirebaseAuth.getInstance().currentUser
                    ?: throw Exception("Not logged in")
                
                val timestamp = System.currentTimeMillis()
                
                // Update friendship status for both users
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(currentUser.uid)
                    .collection("friendships")
                    .document(fromUserId)
                    .set(
                        mapOf(
                            "status" to "accepted",
                            "timestamp" to timestamp
                        )
                    )
                    .await()

                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(fromUserId)
                    .collection("friendships")
                    .document(currentUser.uid)
                    .set(
                        mapOf(
                            "status" to "accepted",
                            "timestamp" to timestamp
                        )
                    )
                    .await()

            } catch (e: Exception) {
                _error.value = "Failed to accept friend request: ${e.message}"
            }
        }
    }
    
    fun declineFriendRequest(fromUserId: String) {
        viewModelScope.launch {
            try {
                val currentUser = FirebaseAuth.getInstance().currentUser
                    ?: throw Exception("Not logged in")
                
                // Delete friendship documents for both users
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(currentUser.uid)
                    .collection("friendships")
                    .document(fromUserId)
                    .delete()
                    .await()

                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(fromUserId)
                    .collection("friendships")
                    .document(currentUser.uid)
                    .delete()
                    .await()

            } catch (e: Exception) {
                _error.value = "Failed to decline friend request: ${e.message}"
            }
        }
    }
} 