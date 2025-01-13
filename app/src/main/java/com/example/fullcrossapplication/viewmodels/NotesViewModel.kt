package com.example.fullcrossapplication.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fullcrossapplication.data.AppDatabase
import com.example.fullcrossapplication.data.Note
import com.example.fullcrossapplication.data.NoteType
import com.example.fullcrossapplication.data.Discussion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID

class NotesViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val noteDao = database.noteDao()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate = _selectedDate.asStateFlow()

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes = _notes.asStateFlow()

    private val _datesWithNotes = MutableStateFlow<Set<LocalDate>>(emptySet())
    val datesWithNotes = _datesWithNotes.asStateFlow()

    private val _discussions = MutableStateFlow<List<Discussion>>(emptyList())
    val discussions = _discussions.asStateFlow()

    init {
        loadNotesForDate(LocalDate.now())
        loadDatesWithNotes()
        loadDiscussions()
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
                    commentCount = 3
                ),
                Discussion(
                    id = "2",
                    title = "Daily Prayer Habits",
                    content = "What are your daily prayer routines?",
                    authorName = "Jane Smith",
                    likes = 8,
                    commentCount = 12
                )
            )
        }
    }

    fun addDiscussion(title: String, content: String) {
        viewModelScope.launch {
            val newDiscussion = Discussion(
                id = UUID.randomUUID().toString(),
                title = title,
                content = content,
                authorId = "current_user_id",
                authorName = "Current User"
            )
            _discussions.value = _discussions.value + newDiscussion
        }
    }
} 