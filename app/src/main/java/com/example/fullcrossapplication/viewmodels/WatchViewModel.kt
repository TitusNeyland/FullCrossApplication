package com.example.fullcrossapplication.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fullcrossapplication.data.BibleRepository
import com.example.fullcrossapplication.data.VerseOfDay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class WatchViewModel : ViewModel() {
    private val repository = BibleRepository()
    private val bibleId = "de4e12af7f28f599-02" // English Standard Version (ESV)
    
    private val _verseOfDay = MutableStateFlow<VerseOfDay?>(null)
    val verseOfDay = _verseOfDay.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

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
} 