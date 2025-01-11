package com.example.fullcrossapplication.data

enum class FriendshipStatus {
    NONE,
    PENDING,
    ACCEPTED,
    DECLINED
}

data class UserProfile(
    val id: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val friendshipStatus: FriendshipStatus = FriendshipStatus.NONE
) {
    val fullName: String
        get() = "$firstName $lastName"
} 