package com.xneo.app.data.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

data class AuthResponse(
    val token: String,
    val refreshToken: String,
    val user: User
)

data class User(
    val id: String,
    val username: String,
    val email: String,
    val avatarUrl: String?,
    val bio: String?,
    val role: String,
    val createdAt: String,
    val followersCount: Int,
    val followingCount: Int,
    val videosCount: Int
)

data class ErrorResponse(
    val message: String,
    val errors: Map<String, String>? = null
)
