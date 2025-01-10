package com.example.fullcrossapplication.data

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface BibleApi {
    @GET("v1/bibles")
    suspend fun getBibles(
        @Header("api-key") apiKey: String
    ): BiblesResponse

    @GET("v1/bibles/{bibleId}/books")
    suspend fun getBooks(
        @Header("api-key") apiKey: String,
        @Path("bibleId") bibleId: String
    ): BooksResponse

    @GET("v1/bibles/{bibleId}/chapters/{chapterId}")
    suspend fun getChapter(
        @Header("api-key") apiKey: String,
        @Path("bibleId") bibleId: String,
        @Path("chapterId") chapterId: String
    ): ChapterResponse

    @GET("v1/bibles/{bibleId}/verses/{verseId}")
    suspend fun getVerse(
        @Header("api-key") apiKey: String,
        @Path("bibleId") bibleId: String,
        @Path("verseId") verseId: String
    ): VerseResponse
}

data class BiblesResponse(
    val data: List<Bible>
)

data class Bible(
    val id: String,
    val name: String,
    val description: String,
    val language: Language
)

data class Language(
    val id: String,
    val name: String
)

data class BooksResponse(
    val data: List<Book>
)

data class Book(
    val id: String,
    val name: String,
    val chapters: List<ChapterSummary>
)

data class ChapterSummary(
    val id: String,
    val number: String
)

data class ChapterResponse(
    val data: Chapter
)

data class Chapter(
    val id: String,
    val number: String,
    val content: String
)

data class VerseResponse(
    val data: VerseData
)

data class VerseData(
    val id: String,
    val orgId: String,
    val reference: String,
    val content: String
) 