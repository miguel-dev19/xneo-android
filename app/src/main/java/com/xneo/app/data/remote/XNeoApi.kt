package com.xneo.app.data.remote

import com.xneo.app.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface XNeoApi {
    @GET("api/videos/category/{category}")
    suspend fun getVideosByCategory(
        @Path("category") category: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<VideoResponse>
    
    @GET("api/videos/{id}")
    suspend fun getVideoById(
        @Path("id") videoId: String
    ): Response<Video>
    
    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>
    
    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>
    
    @GET("api/users/check-username/{username}")
    suspend fun checkUsername(
        @Path("username") username: String
    ): Response<Map<String, Boolean>>
}
