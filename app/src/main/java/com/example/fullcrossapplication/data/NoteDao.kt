package com.example.fullcrossapplication.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE date = :date AND userId = :userId")
    fun getNotesForDate(date: LocalDate, userId: String): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE userId = :userId")
    fun getAllNotes(userId: String): Flow<List<Note>>

    @Insert
    suspend fun insertNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("SELECT DISTINCT date FROM notes WHERE userId = :userId AND EXISTS (SELECT 1 FROM notes n2 WHERE n2.date = notes.date AND n2.userId = :userId)")
    fun getDatesWithNotes(userId: String): Flow<List<LocalDate>>
} 