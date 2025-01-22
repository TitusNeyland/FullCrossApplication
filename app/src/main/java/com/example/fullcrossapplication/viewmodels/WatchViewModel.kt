package com.example.fullcrossapplication.viewmodels

import android.app.Application
import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fullcrossapplication.repository.BibleRepository
import com.example.fullcrossapplication.data.NoteType
import com.example.fullcrossapplication.components.VerseOfDay
import com.example.fullcrossapplication.screens.LiveStream
import com.example.fullcrossapplication.data.StreamSettings
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.jsoup.Jsoup
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.TimeZone

enum class ChatTab {
    CHAT, NOTES
}

class WatchViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val repository = BibleRepository()
    private val bibleId = "de4e12af7f28f599-02" // English Standard Version (ESV)
    
    private val _verseOfDay = MutableStateFlow<VerseOfDay?>(null)
    val verseOfDay = _verseOfDay.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _viewerCount = MutableStateFlow(0)
    val viewerCount: StateFlow<Int> = _viewerCount.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages = _chatMessages.asStateFlow()

    private val _selectedTab = MutableStateFlow(ChatTab.CHAT)
    val selectedTab = _selectedTab.asStateFlow()

    private val _showNoteDialog = MutableStateFlow(false)
    val showNoteDialog = _showNoteDialog.asStateFlow()

    private val notesViewModel = NotesViewModel(getApplication())

    val notes = notesViewModel.notes

    private val firestore = FirebaseFirestore.getInstance()
    
    private val _streamSettings = MutableStateFlow<StreamSettings?>(null)
    val streamSettings: StateFlow<StreamSettings?> = _streamSettings.asStateFlow()

    // List of inspiring Bible verses
    private val verseIds = listOf(
        "JHN.3.16", // John 3:16
        "PHP.4.13", // Philippians 4:13
        "PRO.3.5",  // Proverbs 3:5
        "PSA.23.1", // Psalm 23:1
        "ROM.8.28", // Romans 8:28
        "JER.29.11", // Jeremiah 29:11
        "ISA.41.10", // Isaiah 41:10
        "MAT.11.28", // Matthew 11:28
        "JOS.1.9",   // Joshua 1:9
        "HEB.11.1"   // Hebrews 11:1
    )

    init {
        fetchVerseOfDay()
        viewModelScope.launch {
            while(true) {
                delay(30000) // Update every 30 seconds
                _viewerCount.value = (15..31).random() // Random viewer count between 15-31
            }
        }
        loadStreamSettings()
    }

    private fun fetchVerseOfDay() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Get today's date to use as a seed for verse selection
                val dayOfYear = LocalDate.now().dayOfYear
                // Use the day of year to select a verse (this ensures the same verse shows all day)
                val verseId = verseIds[dayOfYear % verseIds.size]
                
                val response = repository.getVerse(bibleId, verseId)
                val verseData = response.data
                
                // Parse HTML content to plain text
                val plainText = Jsoup.parse(verseData.content).text()
                
                _verseOfDay.value = VerseOfDay(
                    text = plainText,
                    reference = verseData.reference,
                    date = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
                )
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to fetch verse of the day"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshVerse() {
        fetchVerseOfDay()
    }

    fun sendChatMessage(message: String, userName: String = "User") {
        val newMessage = ChatMessage(
            userName = userName,
            message = message,
            timestamp = System.currentTimeMillis()
        )
        _chatMessages.value = _chatMessages.value + newMessage
    }

    fun addReaction(messageId: String, emoji: String) {
        val currentMessages = _chatMessages.value
        val updatedMessages = currentMessages.map { message ->
            if (message.id == messageId) {
                val currentCount = message.reactions[emoji] ?: 0
                message.copy(
                    reactions = message.reactions + (emoji to currentCount + 1)
                )
            } else {
                message
            }
        }
        _chatMessages.value = updatedMessages
    }

    fun addReply(messageId: String, reply: String, userName: String = "User") {
        val currentMessages = _chatMessages.value
        val updatedMessages = currentMessages.map { message ->
            if (message.id == messageId) {
                message.copy(
                    replies = message.replies + ChatReply(
                        userName = userName,
                        message = reply,
                        timestamp = System.currentTimeMillis()
                    )
                )
            } else {
                message
            }
        }
        _chatMessages.value = updatedMessages
    }

    fun setSelectedTab(tab: ChatTab) {
        _selectedTab.value = tab
    }

    fun addNote(title: String, content: String, verseReference: String? = null, type: NoteType = NoteType.GENERAL) {
        notesViewModel.addNote(title, content, verseReference, type)
    }

    fun showNoteDialog() {
        _showNoteDialog.value = true
    }

    fun hideNoteDialog() {
        _showNoteDialog.value = false
    }

    fun onNoteAdded(title: String, content: String) {
        addNote(title, content)
        hideNoteDialog()
    }

    fun setReminder(context: Context, stream: LiveStream) {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, stream.title)
            putExtra(
                CalendarContract.Events.DESCRIPTION, 
                """
                Join us for ${stream.title}
                
                Watch live at: https://www.facebook.com/profile.php?id=100079371798055
                """.trimIndent()
            )
            putExtra(CalendarContract.Events.EVENT_LOCATION, "Facebook Live")
            
            // Convert LocalDateTime to milliseconds
            val beginTime = stream.startTime.atZone(TimeZone.getDefault().toZoneId()).toInstant().toEpochMilli()
            val endTime = beginTime + (stream.durationMinutes * 60 * 1000)
            
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime)
            
            // Add reminder 15 minutes before
            putExtra(CalendarContract.Events.HAS_ALARM, 1)
            putExtra(CalendarContract.Reminders.MINUTES, 15)
            
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        
        context.startActivity(intent)
    }

    private fun loadStreamSettings() {
        viewModelScope.launch {
            try {
                val snapshot = firestore.collection("settings")
                    .document("stream")
                    .get()
                    .await()

                _streamSettings.value = snapshot.toObject(StreamSettings::class.java)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}

data class ChatMessage(
    val id: String = System.currentTimeMillis().toString(),
    val userName: String,
    val message: String,
    val timestamp: Long,
    val reactions: Map<String, Int> = mapOf(),
    val replies: List<ChatReply> = listOf()
)

data class ChatReply(
    val userName: String,
    val message: String,
    val timestamp: Long
) 