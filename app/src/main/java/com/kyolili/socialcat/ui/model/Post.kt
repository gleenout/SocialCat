package com.kyolili.socialcat.model

data class Post(
    val id: String = "", // Add a unique identifier for the post
    val username: String = "",
    val profileImage: Int = 0,
    val postImage: String = "",
    val description: String = "",
    val time: String = "",
    val likes: Int = 0, // Add likes count
    val likedBy: List<String> = listOf() // List of user IDs who liked the post
)