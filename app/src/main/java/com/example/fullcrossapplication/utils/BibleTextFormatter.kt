package com.example.fullcrossapplication.utils

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

object BibleTextFormatter {
    data class FormattedVerse(
        val text: String,
        val verseNumber: String,
        val isVerseStart: Boolean = false
    )

    fun formatBibleText(htmlContent: String): List<FormattedVerse> {
        val document: Document = Jsoup.parse(htmlContent)
        val formattedVerses = mutableListOf<FormattedVerse>()
        
        // Process each verse
        document.select("span.v").forEach { verse ->
            val verseNumber = verse.text()
            verse.text(" ")  // Clear the verse number
            
            // Get the text until the next verse marker
            var verseText = ""
            var current = verse.nextSibling()
            while (current != null && !isVerseMarker(current)) {
                verseText += current.toString()
                current = current.nextSibling()
            }
            
            // Clean up the verse text
            verseText = verseText.replace(Regex("<[^>]*>"), "")
                .replace("  ", " ")
                .trim()
            
            formattedVerses.add(FormattedVerse(verseText, verseNumber, true))
        }
        
        return formattedVerses
    }

    private fun isVerseMarker(node: org.jsoup.nodes.Node): Boolean {
        return node is org.jsoup.nodes.Element && node.hasClass("v")
    }
} 