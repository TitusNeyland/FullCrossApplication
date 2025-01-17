package com.example.fullcrossapplication.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fullcrossapplication.data.AppDatabase
import com.example.fullcrossapplication.data.Bible
import com.example.fullcrossapplication.repository.BibleRepository
import com.example.fullcrossapplication.data.Book
import com.example.fullcrossapplication.data.Chapter
import com.example.fullcrossapplication.data.Note
import com.example.fullcrossapplication.data.NoteType
import com.example.fullcrossapplication.components.VerseOfDay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class BibleViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = BibleRepository()
    private val noteDao = AppDatabase.getDatabase(application).noteDao()

    private val defaultBibleId = "de4e12af7f28f599-02" // English Standard Version (ESV)

    private val _bibles = MutableStateFlow<List<Bible>>(emptyList())
    val bibles = _bibles.asStateFlow()

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books = _books.asStateFlow()

    private val _currentChapter = MutableStateFlow<Chapter?>(null)
    val currentChapter = _currentChapter.asStateFlow()

    private val _currentChapterNumber = MutableStateFlow<Int?>(null)
    val currentChapterNumber = _currentChapterNumber.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _selectedBible = MutableStateFlow<Bible?>(null)
    val selectedBible = _selectedBible.asStateFlow()

    private val _selectedBook = MutableStateFlow<Book?>(null)
    val selectedBook = _selectedBook.asStateFlow()

    private val _selectedVerse = MutableStateFlow<String?>(null)
    val selectedVerse = _selectedVerse.asStateFlow()

    private val _verseOfDay = MutableStateFlow<VerseOfDay?>(null)
    val verseOfDay = _verseOfDay.asStateFlow()

    private val _isLoadingVerse = MutableStateFlow(false)
    val isLoadingVerse = _isLoadingVerse.asStateFlow()

    private val _verseError = MutableStateFlow<String?>(null)
    val verseError = _verseError.asStateFlow()

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
        loadBibles()
        fetchVerseOfDay()
    }

    private fun loadBibles() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _bibles.value = repository.getBibles().data
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadBooks(bibleId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _books.value = repository.getBooks(bibleId).data
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadChapter(bibleId: String, chapterId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _currentChapter.value = repository.getChapter(bibleId, chapterId).data
                _currentChapterNumber.value = _currentChapter.value?.number?.toIntOrNull()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadNextChapter(bibleId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val currentNumber = _currentChapterNumber.value
                if (currentNumber != null) {
                    val nextChapterId = "${_books.value.firstOrNull()?.id}.${currentNumber + 1}"
                    _currentChapter.value = repository.getChapter(bibleId, nextChapterId).data
                    _currentChapterNumber.value = currentNumber + 1
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadPreviousChapter(bibleId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val currentNumber = _currentChapterNumber.value
                if (currentNumber != null && currentNumber > 1) {
                    val previousChapterId = "${_books.value.firstOrNull()?.id}.${currentNumber - 1}"
                    _currentChapter.value = repository.getChapter(bibleId, previousChapterId).data
                    _currentChapterNumber.value = currentNumber - 1
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearChapter() {
        _currentChapter.value = null
        _currentChapterNumber.value = null
        _selectedBook.value = null
    }

    fun setSelectedBible(bible: Bible?) {
        _selectedBible.value = bible
        if (bible != null) {
            loadBooks(bible.id) // Load books for the selected Bible
        } else {
            _books.value = emptyList() // Clear books if no Bible is selected
        }
    }

    fun setSelectedBook(book: Book?) {
        _selectedBook.value = book
    }

    fun setSelectedVerse(verse: String) {
        _selectedVerse.value = verse
    }

    fun addVerseNote(title: String, content: String, verseReference: String) {
        viewModelScope.launch {
            val note = Note(
                date = LocalDate.now(),
                title = title,
                content = content,
                verseReference = verseReference,
                type = NoteType.VERSE
            )
            noteDao.insertNote(note)
            _selectedVerse.value = null // Reset selected verse after adding note
        }
    }

    fun refreshVerseOfDay() {
        fetchVerseOfDay()
    }

    private fun fetchVerseOfDay() {
        viewModelScope.launch {
            _isLoadingVerse.value = true
            _verseError.value = null
            try {
                val dayOfYear = LocalDate.now().dayOfYear
                val verseId = verseIds[dayOfYear % verseIds.size]
                
                val response = repository.getVerse(defaultBibleId, verseId)
                val verseData = response.data
                
                val plainText = Jsoup.parse(verseData.content).text()
                
                _verseOfDay.value = VerseOfDay(
                    text = plainText,
                    reference = verseData.reference,
                    date = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
                )
            } catch (e: Exception) {
                _verseError.value = e.message ?: "Failed to fetch verse of the day"
            } finally {
                _isLoadingVerse.value = false
            }
        }
    }
} 