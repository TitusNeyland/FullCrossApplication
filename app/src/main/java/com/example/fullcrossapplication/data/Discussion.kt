package com.example.fullcrossapplication.data

data class Discussion(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val likes: Int = 0,
    val commentCount: Int = 0,
    val tags: List<String> = emptyList(),
    val comments: List<Comment> = emptyList(),
    val likedByUsers: Set<String> = emptySet()
)

data class Comment(
    val id: String = "",
    val discussionId: String = "",
    val content: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val likes: Int = 0,
    val parentCommentId: String? = null,
    val replyToAuthorName: String? = null,
    val replyCount: Int = 0,
    val isReply: Boolean = false
) 