package com.example.fullcrossapplication.repository

import android.content.Context
import android.provider.ContactsContract
import com.example.fullcrossapplication.data.Contact
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ContactsRepository(private val context: Context) {
    private val firestore = FirebaseFirestore.getInstance()
    
    suspend fun getContacts(): List<Contact> = withContext(Dispatchers.IO) {
        val contacts = mutableListOf<Contact>()
        val appUsers = getAppUsers()
        
        context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Email.ADDRESS
            ),
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))
                val name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    ?.replace("[^0-9]".toRegex(), "") // Clean phone number format
                
                // Check if contact already exists to avoid duplicates
                if (contacts.none { it.id == id }) {
                    contacts.add(
                        Contact(
                            id = id,
                            name = name,
                            phoneNumber = number,
                            email = null,
                            isAppUser = appUsers.contains(number)
                        )
                    )
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