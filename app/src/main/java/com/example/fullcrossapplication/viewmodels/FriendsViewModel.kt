package com.example.fullcrossapplication.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fullcrossapplication.data.UserProfile
import com.example.fullcrossapplication.data.FriendshipStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FriendsViewModel : ViewModel() {
    private val _friends = MutableStateFlow<List<UserProfile>>(emptyList())
    val friends: StateFlow<List<UserProfile>> = _friends

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadFriends() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                    ?: throw Exception("User not logged in")
                
                val friendshipsSnapshot = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(currentUserId)
                    .collection("friendships")
                    .get()
                    .await()

                val friendsList = mutableListOf<UserProfile>()
                
                friendshipsSnapshot.documents.forEach { doc ->
                    val friendId = doc.id
                    // Skip if somehow the user is in their own friends list
                    if (friendId == currentUserId) return@forEach
                    
                    val status = when(doc.getString("status")) {
                        "pending" -> FriendshipStatus.PENDING
                        "accepted" -> FriendshipStatus.ACCEPTED
                        "declined" -> FriendshipStatus.DECLINED
                        else -> FriendshipStatus.NONE
                    }
                    
                    val userDoc = FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(friendId)
                        .get()
                        .await()
                    
                    userDoc.data?.let { userData ->
                        friendsList.add(
                            UserProfile(
                                id = friendId,
                                firstName = userData["firstName"] as? String ?: "",
                                lastName = userData["lastName"] as? String ?: "",
                                phoneNumber = userData["phoneNumber"] as? String ?: "",
                                friendshipStatus = status
                            )
                        )
                    }
                }
                
                _friends.value = friendsList
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun sendFriendRequest(userId: String) {
        viewModelScope.launch {
            try {
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                    ?: throw Exception("User not logged in")
                
                // Prevent self-friend requests
                if (currentUserId == userId) {
                    _error.value = "You cannot send a friend request to yourself"
                    return@launch
                }
                
                val timestamp = System.currentTimeMillis()
                
                // Add to current user's friendships
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(currentUserId)
                    .collection("friendships")
                    .document(userId)
                    .set(
                        mapOf(
                            "status" to "pending",
                            "createdAt" to timestamp,
                            "updatedAt" to timestamp
                        )
                    )
                    .await()
                
                // Add to recipient's friendships
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userId)
                    .collection("friendships")
                    .document(currentUserId)
                    .set(
                        mapOf(
                            "status" to "pending",
                            "createdAt" to timestamp,
                            "updatedAt" to timestamp
                        )
                    )
                    .await()
                
                loadFriends() // Refresh the friends list
            } catch (e: Exception) {
                _error.value = "Failed to send friend request: ${e.message}"
            }
        }
    }

    fun acceptFriendRequest(userId: String) {
        viewModelScope.launch {
            try {
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                    ?: throw Exception("User not logged in")
                
                val timestamp = System.currentTimeMillis()
                
                // Update current user's friendship status
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(currentUserId)
                    .collection("friendships")
                    .document(userId)
                    .update(
                        mapOf(
                            "status" to "accepted",
                            "updatedAt" to timestamp
                        )
                    )
                    .await()
                
                // Update other user's friendship status
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userId)
                    .collection("friendships")
                    .document(currentUserId)
                    .update(
                        mapOf(
                            "status" to "accepted",
                            "updatedAt" to timestamp
                        )
                    )
                    .await()
                
                loadFriends() // Refresh the friends list
            } catch (e: Exception) {
                _error.value = "Failed to accept friend request: ${e.message}"
            }
        }
    }

    fun declineFriendRequest(userId: String) {
        viewModelScope.launch {
            try {
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                    ?: throw Exception("User not logged in")
                
                val timestamp = System.currentTimeMillis()
                val batch = FirebaseFirestore.getInstance().batch()
                
                // Update both users' friendship status
                val currentUserRef = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(currentUserId)
                    .collection("friendships")
                    .document(userId)
                
                val otherUserRef = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userId)
                    .collection("friendships")
                    .document(currentUserId)
                
                batch.delete(currentUserRef)
                batch.delete(otherUserRef)
                
                batch.commit().await()
                
                loadFriends() // Refresh the friends list
            } catch (e: Exception) {
                _error.value = "Failed to decline friend request: ${e.message}"
            }
        }
    }
} 