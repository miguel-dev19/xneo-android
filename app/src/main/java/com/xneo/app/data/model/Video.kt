package com.xneo.app.data.model

data class Video(
    val id: String,
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val streamUrl: String,
    val duration: Int,
    val views: Int,
    val likes: Int,
    val dislikes: Int,
    val category: String,
    val tags: List<String>,
    val uploadDate: String,
    val user: VideoUser,
    val qualities: List<String>
)

data class VideoUser(
    val username: String,
    val avatarUrl: String
)

data class VideoResponse(
    val videos: List<Video>,
    val page: Int,
    val totalPages: Int,
    val hasMore: Boolean
)
