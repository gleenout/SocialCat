package com.kyolili.socialcat.model

data class Post(
    val username: String,
    val profileImage: Int,
    val postImage: Int,
    val description: String,
    val time: String,
    var isLiked: Boolean = false
)
