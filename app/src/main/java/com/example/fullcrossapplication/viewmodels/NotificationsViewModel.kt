package com.example.fullcrossapplication.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fullcrossapplication.data.Notification
import com.example.fullcrossapplication.data.NotificationType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class NotificationsViewModel : ViewModel() {
    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            try {
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                    ?: return@launch

                // Set up real-time listener for notifications
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(currentUserId)
                    .collection("notifications")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            return@addSnapshotListener
                        }

                        val notificationsList = snapshot?.documents?.mapNotNull { doc ->
                            try {
                                Notification(
                                    id = doc.id,
                                    type = NotificationType.valueOf(
                                        doc.getString("type") ?: NotificationType.FRIEND_REQUEST.name
                                    ),
                                    fromUserId = doc.getString("fromUserId") ?: "",
                                    fromUserName = doc.getString("fromUserName") ?: "",
                                    timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis(),
                                    read = doc.getBoolean("read") ?: false
                                )
                            } catch (e: Exception) {
                                null
                            }
                        } ?: emptyList()

                        _notifications.value = notificationsList
                        _unreadCount.value = notificationsList.count { !it.read }
                    }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            try {
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                    ?: return@launch

                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(currentUserId)
                    .collection("notifications")
                    .document(notificationId)
                    .update("read", true)
                    .await()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
} 