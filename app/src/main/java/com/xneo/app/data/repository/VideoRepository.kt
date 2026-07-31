package com.xneo.app.data.repository

import com.xneo.app.data.model.Video
import com.xneo.app.data.model.VideoResponse
import com.xneo.app.data.remote.XNeoApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoRepository @Inject constructor(
    private val api: XNeoApi
) {
    suspend fun getVideosByCategory(category: String, page: Int = 1): Result<VideoResponse> {
        return try {
            val response = api.getVideosByCategory(category, page)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexion. Verifica tu internet e intenta de nuevo."))
        }
    }
    
    suspend fun getVideoById(videoId: String): Result<Video> {
        return try {
            val response = api.getVideoById(videoId)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else if (response.code() == 404) {
                Result.failure(Exception("Este video no esta disponible o fue eliminado."))
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexion. Verifica tu internet e intenta de nuevo."))
        }
    }
}
