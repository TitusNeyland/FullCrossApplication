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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel

class NotesViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val noteDao = database.noteDao()
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private var authStateListener: FirebaseAuth.AuthStateListener? = null

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

    private var discussionsListener: com.google.firebase.firestore.ListenerRegistration? = null

    init {
        setupAuthStateListener()
        if (auth.currentUser != null) {
            refreshAllData()
        }
    }

    private fun setupAuthStateListener() {
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
                refreshAllData()
            } else {
                clearAllData()
            }
        }
        authStateListener?.let { auth.addAuthStateListener(it) }
    }

    override fun onCleared() {
        super.onCleared()
        authStateListener?.let { auth.removeAuthStateListener(it) }
        discussionsListener?.remove()
        viewModelScope.cancel()
    }

    private fun clearAllData() {
        viewModelScope.launch {
            _notes.value = emptyList()
            _datesWithNotes.value = emptySet()
            _currentUserName.value = null
            _selectedDate.value = LocalDate.now()
        }
    }

    private fun refreshAllData() {
        loadNotesForDate(LocalDate.now())
        loadDatesWithNotes()
        loadDiscussions()
        loadCurrentUserName()
    }

    private fun getCurrentUserId(): String {
        return auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")
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
            try {
                val userId = getCurrentUserId()
                noteDao.getNotesForDate(date, userId).collect {
                    _notes.value = it
                }
            } catch (e: IllegalStateException) {
                // Handle not logged in state
                _notes.value = emptyList()
            }
        }
    }

    fun addNote(title: String, content: String, verseReference: String?, type: NoteType) {
        viewModelScope.launch {
            try {
                val userId = getCurrentUserId()
                val note = Note(
                    date = LocalDate.now(),
                    title = title,
                    content = content,
                    verseReference = verseReference,
                    type = type,
                    userId = userId
                )
                noteDao.insertNote(note)
                setSelectedDate(LocalDate.now())
            } catch (e: IllegalStateException) {
                // Handle not logged in state
            }
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            try {
                val userId = getCurrentUserId()
                if (note.userId == userId) {
                    noteDao.deleteNote(note)
                }
            } catch (e: IllegalStateException) {
                // Handle not logged in state
            }
        }
    }

    private fun loadDatesWithNotes() {
        viewModelScope.launch {
            try {
                val userId = getCurrentUserId()
                noteDao.getDatesWithNotes(userId).collect { dates ->
                    _datesWithNotes.value = dates.toSet()
                }
            } catch (e: IllegalStateException) {
                // Handle not logged in state
                _datesWithNotes.value = emptySet()
            }
        }
    }

    private fun loadDiscussions() {
        discussionsListener?.remove()
        discussionsListener = firestore.collection("discussions")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                viewModelScope.launch {
                    try {
                        val discussionsWithComments = snapshot?.documents?.map { doc ->
                            // First create the discussion
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

                            // Set up real-time listener for comments
                            doc.reference.collection("comments")
                                .orderBy("timestamp")
                                .addSnapshotListener { commentsSnapshot, commentsError ->
                                    if (commentsError != null) {
                                        return@addSnapshotListener
                                    }

                                    val comments = commentsSnapshot?.documents?.mapNotNull { commentDoc ->
                                        val comment = Comment(
                                            id = commentDoc.id,
                                            discussionId = doc.id,
                                            content = commentDoc.getString("content") ?: "",
                                            authorId = commentDoc.getString("authorId") ?: "",
                                            authorName = commentDoc.getString("authorName") ?: "",
                                            timestamp = commentDoc.getLong("timestamp") ?: System.currentTimeMillis(),
                                            likes = commentDoc.getLong("likes")?.toInt() ?: 0,
                                            parentCommentId = commentDoc.getString("parentCommentId"),
                                            replyToAuthorName = commentDoc.getString("replyToAuthorName"),
                                            replyCount = commentDoc.getLong("replyCount")?.toInt() ?: 0,
                                            isReply = commentDoc.getBoolean("isReply") ?: false
                                        )
                                        comment
                                    } ?: emptyList()

                                    // Group comments by parent ID to organize replies
                                    val commentMap = comments.groupBy { it.parentCommentId }
                                    
                                    // Get top-level comments (no parent)
                                    val topLevelComments = commentMap[null] ?: emptyList()
                                    
                                    // Create a list with all comments in the correct order
                                    val orderedComments = topLevelComments.flatMap { parentComment ->
                                        listOf(parentComment) + (commentMap[parentComment.id] ?: emptyList())
                                    }

                                    // Find the current discussion in the list and update its comments
                                    val currentDiscussions = _discussions.value
                                    val updatedDiscussions = currentDiscussions.map { existingDiscussion ->
                                        if (existingDiscussion.id == doc.id) {
                                            existingDiscussion.copy(comments = orderedComments)
                                        } else {
                                            existingDiscussion
                                        }
                                    }
                                    _discussions.value = updatedDiscussions
                                }

                            discussion
                        } ?: emptyList()

                        // Only update the discussions list if it's empty or if there are changes
                        if (_discussions.value.isEmpty()) {
                            _discussions.value = discussionsWithComments
                        } else {
                            // Update only the discussions list metadata, keeping existing comments
                            val updatedDiscussions = _discussions.value.map { existingDiscussion ->
                                discussionsWithComments.find { it.id == existingDiscussion.id }
                                    ?.copy(comments = existingDiscussion.comments)
                                    ?: existingDiscussion
                            }
                            _discussions.value = updatedDiscussions
                        }
                    } catch (e: Exception) {
                        // Handle error
                    }
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

    fun addComment(discussionId: String, content: String, parentCommentId: String? = null, replyToAuthorName: String? = null) {
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
                    "likes" to 0,
                    "parentCommentId" to parentCommentId,
                    "replyToAuthorName" to replyToAuthorName,
                    "replyCount" to 0,
                    "isReply" to (parentCommentId != null)
                )

                // Add comment to subcollection
                val commentRef = firestore.collection("discussions").document(discussionId)
                    .collection("comments")
                    .add(commentData)
                    .await()

                // Update comment count on discussion
                firestore.collection("discussions").document(discussionId)
                    .update("commentCount", FieldValue.increment(1))
                    .await()

                // If this is a reply, update the parent comment's reply count
                if (parentCommentId != null) {
                    firestore.collection("discussions").document(discussionId)
                        .collection("comments")
                        .document(parentCommentId)
                        .update("replyCount", FieldValue.increment(1))
                        .await()
                }

            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun deleteComment(discussionId: String, commentId: String) {
        viewModelScope.launch {
            try {
                // Get the comment to check if it's a reply and get its parent comment ID
                val commentDoc = firestore.collection("discussions")
                    .document(discussionId)
                    .collection("comments")
                    .document(commentId)
                    .get()
                    .await()

                val isReply = commentDoc.getBoolean("isReply") ?: false
                val parentCommentId = commentDoc.getString("parentCommentId")
                val replyCount = commentDoc.getLong("replyCount")?.toInt() ?: 0

                // Start a batch write
                val batch = firestore.batch()
                val discussionRef = firestore.collection("discussions").document(discussionId)
                val commentRef = discussionRef.collection("comments").document(commentId)

                // If this is a parent comment with replies, delete all replies first
                if (!isReply && replyCount > 0) {
                    // Get all replies to this comment
                    val replies = discussionRef.collection("comments")
                        .whereEqualTo("parentCommentId", commentId)
                        .get()
                        .await()

                    // Add all reply deletions to the batch
                    replies.documents.forEach { replyDoc ->
                        batch.delete(replyDoc.reference)
                    }

                    // Update discussion comment count to account for deleted replies
                    batch.update(discussionRef, "commentCount", FieldValue.increment(-(replies.size() + 1).toLong()))
                } else if (isReply && parentCommentId != null) {
                    // If this is a reply, update the parent comment's reply count
                    val parentCommentRef = discussionRef.collection("comments").document(parentCommentId)
                    batch.update(parentCommentRef, "replyCount", FieldValue.increment(-1L))
                    // Update discussion comment count for the single reply
                    batch.update(discussionRef, "commentCount", FieldValue.increment(-1L))
                } else {
                    // For a regular comment with no replies
                    batch.update(discussionRef, "commentCount", FieldValue.increment(-1L))
                }

                // Delete the comment itself
                batch.delete(commentRef)

                // Commit all the changes
                batch.commit().await()

            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun deleteDiscussion(discussionId: String) {
        viewModelScope.launch {
            try {
                val currentUserId = auth.currentUser?.uid ?: return@launch
                
                // Get the discussion to verify ownership
                val discussion = firestore.collection("discussions")
                    .document(discussionId)
                    .get()
                    .await()

                // Only allow deletion if the current user is the author
                if (discussion.getString("authorId") == currentUserId) {
                    // Delete all comments first
                    val comments = firestore.collection("discussions")
                        .document(discussionId)
                        .collection("comments")
                        .get()
                        .await()

                    // Batch delete all comments
                    val batch = firestore.batch()
                    comments.documents.forEach { comment ->
                        batch.delete(comment.reference)
                    }
                    batch.delete(discussion.reference)
                    batch.commit().await()
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
} 