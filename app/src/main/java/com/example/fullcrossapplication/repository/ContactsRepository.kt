package com.example.fullcrossapplication.repository

import android.content.Context
import android.provider.ContactsContract
import android.database.Cursor
import com.example.fullcrossapplication.data.Contact
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ContactsRepository(private val context: Context) {
    private val firestore = FirebaseFirestore.getInstance()
    
    suspend fun getContacts(): List<Contact> = withContext(Dispatchers.IO) {
        val contacts = mutableListOf<Contact>()
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        
        context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )?.use { cursor ->
            // Get the column indexes safely
            val idIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            
            // Only proceed if all columns exist
            if (idIndex >= 0 && nameIndex >= 0 && numberIndex >= 0) {
                while (cursor.moveToNext()) {
                    val id = cursor.getString(idIndex)
                    val name = cursor.getString(nameIndex)
                    val number = cursor.getString(numberIndex)
                    
                    // Only add contacts that have both name and number
                    if (!name.isNullOrBlank() && !number.isNullOrBlank()) {
                        val cleanNumber = number.replace("[^0-9+]".toRegex(), "")
                        // Check if this number is already in the list to avoid duplicates
                        if (contacts.none { it.phoneNumber == cleanNumber }) {
                            contacts.add(Contact(
                                id = id,
                                name = name,
                                phoneNumber = cleanNumber,
                                isAppUser = false // This will be updated later
                            ))
                        }
                    }
                }
            }
        }
        
        // Update app users status
        val appUsers = getAppUsers()
        contacts.forEach { contact ->
            contact.phoneNumber?.let { phone ->
                if (appUsers.contains(phone)) {
                    contacts[contacts.indexOf(contact)] = contact.copy(isAppUser = true)
                }
            }
        }
        
        contacts
    }

    private suspend fun getAppUsers(): Set<String> = withContext(Dispatchers.IO) {
        try {
            val snapshot = firestore.collection("users")
                .get()
                .await()
            
            return@withContext snapshot.documents.mapNotNull { doc ->
                doc.getString("phoneNumber")
                    ?.replace("[^0-9]".toRegex(), "") // Clean phone number format
            }.toSet()
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext emptySet()
        }
    }
} 