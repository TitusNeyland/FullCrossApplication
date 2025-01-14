package com.example.fullcrossapplication.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fullcrossapplication.data.AppDatabase
import com.example.fullcrossapplication.data.Note
import com.example.fullcrossapplication.data.NoteType
import com.example.fullcrossapplication.data.Discussion
import com.example.fullcrossapplication.data.Comment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.util.UUID

class NotesViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val noteDao = database.noteDao()
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate = _selectedDate.asStateFlow()

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes = _notes.asStateFlow()

    private val _datesWithNotes = MutableStateFlow<Set<LocalDate>>(emptySet())
    val datesWithNotes = _datesWithNotes.asStateFlow()

    private val _discussions = MutableStateFlow<List<Discussion>>(emptyList())
    val discussions = _discussions.asStateFlow()

    private val _currentUserName = MutableStateFlow<String?>(null)
    val currentUserName = _currentUserName.asStateFlow()

    init {
        loadNotesForDate(LocalDate.now())
        loadDatesWithNotes()
        loadDiscussions()
        loadCurrentUserName()
    }

    private fun loadCurrentUserName() {
        viewModelScope.launch {
            auth.currentUser?.let { user ->
                firestore.collection("users")
                    .document(user.uid)
                    .get()
                    .addOnSuccessListener { document ->
                        val firstName = document.getString("firstName") ?: ""
                        val lastName = document.getString("lastName") ?: ""
                        _currentUserName.value = "$firstName $lastName".trim()
                    }
            }
        }
    }

    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
        loadNotesForDate(date)
    }

    private fun loadNotesForDate(date: LocalDate) {
        viewModelScope.launch {
            noteDao.getNotesForDate(date).collect {
                _notes.value = it
            }
        }
    }

    fun addNote(title: String, content: String, verseReference: String?, type: NoteType) {
        viewModelScope.launch {
            val note = Note(
                date = LocalDate.now(),
                title = title,
                content = content,
                verseReference = verseReference,
                type = type
            )
            noteDao.insertNote(note)
            setSelectedDate(LocalDate.now())
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            noteDao.deleteNote(note)
        }
    }

    private fun loadDatesWithNotes() {
        viewModelScope.launch {
            noteDao.getDatesWithNotes().collect { dates ->
                _datesWithNotes.value = dates.toSet()
            }
        }
    }

    private fun loadDiscussions() {
        viewModelScope.launch {
            try {
                // Listen for real-time updates to discussions
                firestore.collection("discussions")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            // Handle error
                            return@addSnapshotListener
                        }

                        // Launch a new coroutine for processing the snapshot
                        viewModelScope.launch {
                            try {
                                val discussionsList = snapshot?.documents?.map { doc ->
                                    val discussion = Discussion(
                                        id = doc.id,
                                        title = doc.getString("title") ?: "",
                                        content = doc.getString("content") ?: "",
                                        authorId = doc.getString("authorId") ?: "",
                                        authorName = doc.getString("authorName") ?: "",
                                        timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis(),
                                        likes = doc.getLong("likes")?.toInt() ?: 0,
                                        commentCount = doc.getLong("commentCount")?.toInt() ?: 0,
                                        likedByUsers = (doc.get("likedByUsers") as? List<String>)?.toSet() ?: emptySet()
                                    )

                                    // Set up real-time listener for comments of this discussion
                                    doc.reference.collection("comments")
                                        .orderBy("timestamp")
                                        .addSnapshotListener { commentsSnapshot, commentsError ->
                                            if (commentsError != null) {
                                                return@addSnapshotListener
                                            }

                                            viewModelScope.launch {
                                                val comments = commentsSnapshot?.documents?.map { commentDoc ->
                                                    Comment(
                                                        id = commentDoc.id,
                                                        discussionId = doc.id,
                                                        content = commentDoc.getString("content") ?: "",
                                                        authorId = commentDoc.getString("authorId") ?: "",
                                                        authorName = commentDoc.getString("authorName") ?: "",
                                                        timestamp = commentDoc.getLong("timestamp") ?: System.currentTimeMillis(),
                                                        likes = commentDoc.getLong("likes")?.toInt() ?: 0
                                                    )
                                                } ?: emptyList()

                                                // Update the discussions list with new comments
                                                _discussions.value = _discussions.value.map { existingDiscussion ->
                                                    if (existingDiscussion.id == discussion.id) {
                                                        existingDiscussion.copy(
                                                            comments = comments,
                                                            commentCount = comments.size
                                                        )
                                                    } else {
                                                        existingDiscussion
                                                    }
                                                }
                                            }
                                        }

                                    discussion
                                } ?: emptyList()

                                _discussions.value = discussionsList
                            } catch (e: Exception) {
                                // Handle error fetching comments
                            }
                        }
                    }
            } catch (e: Exception) {
                // Handle error setting up listener
            }
        }
    }

    fun addDiscussion(title: String, content: String) {
        viewModelScope.launch {
            try {
                val currentUserId = auth.currentUser?.uid ?: return@launch
                val currentUserName = _currentUserName.value ?: "Anonymous"
                
                val discussionData = hashMapOf(
                    "title" to title,
                    "content" to content,
                    "authorId" to currentUserId,
                    "authorName" to currentUserName,
                    "timestamp" to System.currentTimeMillis(),
                    "likes" to 0,
                    "commentCount" to 0,
                    "likedByUsers" to listOf<String>()
                )

                firestore.collection("discussions")
                    .add(discussionData)
                    .await()

            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun likeDiscussion(discussionId: String) {
        viewModelScope.launch {
            try {
                val currentUserId = auth.currentUser?.uid ?: return@launch
                val discussionRef = firestore.collection("discussions").document(discussionId)
                
                firestore.runTransaction { transaction ->
                    val snapshot = transaction.get(discussionRef)
                    val likedByUsers = snapshot.get("likedByUsers") as? List<String> ?: listOf()
                    val currentLikes = snapshot.getLong("likes")?.toInt() ?: 0
                    
                    if (currentUserId in likedByUsers) {
                        // Unlike
                        transaction.update(discussionRef, 
                            "likedByUsers", likedByUsers - currentUserId,
                            "likes", currentLikes - 1
                        )
                    } else {
                        // Like
                        transaction.update(discussionRef,
                            "likedByUsers", likedByUsers + currentUserId,
                            "likes", currentLikes + 1
                        )
                    }
                }.await()

            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun addComment(discussionId: String, content: String) {
        viewModelScope.launch {
            try {
                val currentUserId = auth.currentUser?.uid ?: return@launch
                
                // Fetch current user's name from Firestore
                val userDoc = firestore.collection("users")
                    .document(currentUserId)
                    .get()
                    .await()
                
                val firstName = userDoc.getString("firstName") ?: ""
                val lastName = userDoc.getString("lastName") ?: ""
                val currentUserName = "$firstName $lastName".trim().ifEmpty { "Anonymous" }
                
                val commentData = hashMapOf(
                    "content" to content,
                    "authorId" to currentUserId,
                    "authorName" to currentUserName,
                    "timestamp" to System.currentTimeMillis(),
                    "likes" to 0
                )

                // Add comment to subcollection
                firestore.collection("discussions").document(discussionId)
                    .collection("comments")
                    .add(commentData)
                    .await()

                // Update comment count
                firestore.collection("discussions").document(discussionId)
                    .update("commentCount", FieldValue.increment(1))
                    .await()

            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun deleteComment(discussionId: String, commentId: String) {
        viewModelScope.launch {
            try {
                // Delete the comment
                firestore.collection("discussions")
                    .document(discussionId)
                    .collection("comments")
                    .document(commentId)
                    .delete()
                    .await()

                // Decrement the comment count
                firestore.collection("discussions")
                    .document(discussionId)
                    .update("commentCount", FieldValue.increment(-1))
                    .await()

            } catch (e: Exception) {
                // Handle error
            }
        }
    }
} 