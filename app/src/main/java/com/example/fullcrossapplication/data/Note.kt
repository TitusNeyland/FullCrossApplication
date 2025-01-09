package com.example.fullcrossapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: LocalDate,
    val title: String,
    val content: String,
    val verseReference: String? = null,
    val type: NoteType
)

enum class NoteType {
    VERSE,
    SERMON,
    GENERAL
} 