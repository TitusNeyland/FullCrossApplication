package com.example.fullcrossapplication.data

data class Contact(
    val id: String = "",
    val name: String,
    val phoneNumber: String?,
    val email: String? = null,
    val isAppUser: Boolean = false
) 