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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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
            _discussions.value = listOf(
                Discussion(
                    id = "1",
                    title = "Understanding Psalms",
                    content = "Let's discuss the deeper meanings in Psalms...",
                    authorName = "John Doe",
                    likes = 5,
                    commentCount = 3,
                    comments = listOf(
                        Comment(
                            id = "c1",
                            discussionId = "1",
                            content = "I find Psalm 23 particularly meaningful...",
                            authorName = "Sarah Wilson",
                            timestamp = System.currentTimeMillis() - 86400000 // 1 day ago
                        ),
                        Comment(
                            id = "c2",
                            discussionId = "1",
                            content = "The psalms of praise always lift my spirit!",
                            authorName = "Michael Brown",
                            timestamp = System.currentTimeMillis() - 43200000 // 12 hours ago
                        )
                    )
                ),
                Discussion(
                    id = "2",
                    title = "Daily Prayer Habits",
                    content = "What are your daily prayer routines?",
                    authorName = "Jane Smith",
                    likes = 8,
                    commentCount = 12,
                    comments = listOf(
                        Comment(
                            id = "c3",
                            discussionId = "2",
                            content = "I start each morning with prayer and meditation.",
                            authorName = "David Lee",
                            timestamp = System.currentTimeMillis() - 7200000 // 2 hours ago
                        )
                    )
                )
            )
        }
    }

    fun addDiscussion(title: String, content: String) {
        viewModelScope.launch {
            val currentUserName = _currentUserName.value ?: "Anonymous"
            val newDiscussion = Discussion(
                id = UUID.randomUUID().toString(),
                title = title,
                content = content,
                authorId = auth.currentUser?.uid ?: "",
                authorName = currentUserName,
                timestamp = System.currentTimeMillis(),
                likes = 0,
                commentCount = 0,
                comments = emptyList()
            )
            _discussions.value = _discussions.value + newDiscussion
        }
    }

    fun likeDiscussion(discussionId: String) {
        viewModelScope.launch {
            val currentDiscussions = _discussions.value
            val updatedDiscussions = currentDiscussions.map { discussion ->
                if (discussion.id == discussionId) {
                    discussion.copy(likes = discussion.likes + 1)
                } else {
                    discussion
                }
            }
            _discussions.value = updatedDiscussions
        }
    }

    fun addComment(discussionId: String, content: String) {
        viewModelScope.launch {
            val currentUserName = _currentUserName.value ?: "Anonymous"
            val newComment = Comment(
                id = UUID.randomUUID().toString(),
                discussionId = discussionId,
                content = content,
                authorId = auth.currentUser?.uid ?: "",
                authorName = currentUserName,
                timestamp = System.currentTimeMillis()
            )

            val currentDiscussions = _discussions.value
            val updatedDiscussions = currentDiscussions.map { discussion ->
                if (discussion.id == discussionId) {
                    discussion.copy(
                        commentCount = discussion.commentCount + 1,
                        comments = discussion.comments + newComment
                    )
                } else {
                    discussion
                }
            }
            _discussions.value = updatedDiscussions
        }
    }
} 