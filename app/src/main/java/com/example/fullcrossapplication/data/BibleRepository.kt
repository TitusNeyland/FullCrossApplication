package com.example.fullcrossapplication.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BibleRepository {
    private val api: BibleApi

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.scripture.api.bible/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(BibleApi::class.java)
    }

    private val apiKey = "7b7279c82199c911590c615bd99cb895" // Get this from API.Bible

    suspend fun getBibles() = api.getBibles(apiKey)
    
    suspend fun getBooks(bibleId: String) = api.getBooks(apiKey, bibleId)
    
    suspend fun getChapter(bibleId: String, chapterId: String) = 
        api.getChapter(apiKey, bibleId, chapterId)
    
    suspend fun getVerse(bibleId: String, verseId: String) = 
        api.getVerse(apiKey, bibleId, verseId)
} 