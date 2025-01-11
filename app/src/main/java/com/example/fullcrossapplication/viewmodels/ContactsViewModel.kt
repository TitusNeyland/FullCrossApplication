package com.example.fullcrossapplication.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fullcrossapplication.data.Contact
import com.example.fullcrossapplication.repository.ContactsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ContactsViewModel(application: Application) : AndroidViewModel(application) {
    private val contactsRepository = ContactsRepository(application)
    
    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> = _contacts
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    fun syncContacts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val contactsList = contactsRepository.getContacts()
                _contacts.value = contactsList
            } catch (e: Exception) {
                _error.value = "Failed to sync contacts: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
} 