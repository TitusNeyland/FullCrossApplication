package com.example.fullcrossapplication.utils

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

object BibleTextFormatter {
    fun formatBibleText(htmlContent: String): String {
        val document: Document = Jsoup.parse(htmlContent)
        
        // Remove all HTML tags except verse numbers and line breaks
        document.select("span.v").forEach { verse ->
            verse.text(" [${verse.text()}] ")
        }
        
        // Convert <p> tags to line breaks
        document.select("p").forEach { paragraph ->
            paragraph.prepend("\n\n")
        }
        
        // Get the cleaned text
        return document.text()
            .replace("  ", " ") // Remove double spaces
            .trim()
    }
} 