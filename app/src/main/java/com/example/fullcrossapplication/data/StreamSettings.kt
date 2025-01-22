package com.example.fullcrossapplication.data

data class StreamSettings(
    val streamUrl: String = "",
    val lastUpdated: Long = System.currentTimeMillis(),
    val updatedBy: String = ""
) 