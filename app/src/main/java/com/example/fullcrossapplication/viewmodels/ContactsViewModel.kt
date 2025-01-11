package com.example.fullcrossapplication.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fullcrossapplication.data.Contact
import com.example.fullcrossapplication.repository.ContactsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.firestore.FirebaseFirestore
import com.example.fullcrossapplication.data.UserProfile
import kotlinx.coroutines.tasks.await

class ContactsViewModel(application: Application) : AndroidViewModel(application) {
    private val contactsRepository = ContactsRepository(application)
    
    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> = _contacts
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery
    
    private val _searchResults = MutableStateFlow<List<UserProfile>>(emptyList())
    val searchResults: StateFlow<List<UserProfile>> = _searchResults
    
    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching
    
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
    
    fun searchUsers(query: String) {
        viewModelScope.launch {
            _searchQuery.value = query
            _isSearching.value = true
            
            try {
                if (query.length >= 2) {
                    val cleanQuery = query.trim().lowercase()
                    FirebaseFirestore.getInstance()
                        .collection("users")
                        .get()
                        .await()
                        .documents
                        .mapNotNull { doc ->
                            val firstName = doc.getString("firstName")?.lowercase() ?: ""
                            val lastName = doc.getString("lastName")?.lowercase() ?: ""
                            val phone = doc.getString("phoneNumber")?.replace("[^0-9]".toRegex(), "") ?: ""
                            
                            if (firstName.contains(cleanQuery) || 
                                lastName.contains(cleanQuery) || 
                                phone.contains(cleanQuery)
                            ) {
                                UserProfile(
                                    id = doc.id,
                                    firstName = doc.getString("firstName") ?: "",
                                    lastName = doc.getString("lastName") ?: "",
                                    phoneNumber = phone
                                )
                            } else null
                        }
                        .also { _searchResults.value = it }
                } else {
                    _searchResults.value = emptyList()
                }
            } catch (e: Exception) {
                _error.value = "Search failed: ${e.message}"
                _searchResults.value = emptyList()
            } finally {
                _isSearching.value = false
            }
        }
    }
    
    fun clearSearch() {
        _searchQuery.value = ""
        _searchResults.value = emptyList()
    }
} 