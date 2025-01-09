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

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

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
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearChapter() {
        _currentChapter.value = null
    }
} 