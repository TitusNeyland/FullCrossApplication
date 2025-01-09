package com.example.fullcrossapplication.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fullcrossapplication.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BibleViewModel : ViewModel() {
    private val repository = BibleRepository()

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

    init {
        loadBibles()
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

    fun loadBooks(bibleId: String) {
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
    }

    fun setSelectedBook(book: Book?) {
        _selectedBook.value = book
    }
} 