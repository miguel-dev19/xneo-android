package com.xneo.app.data.repository

import com.xneo.app.data.local.SessionManager
import com.xneo.app.data.model.*
import com.xneo.app.data.remote.XNeoApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: XNeoApi,
    private val sessionManager: SessionManager
) {
    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful) {
                val authResponse = response.body()!!
                sessionManager.saveSession(authResponse.token, authResponse.user)
                Result.success(authResponse)
            } else {
                val errorBody = response.errorBody()?.string()
                val message = when {
                    errorBody?.contains("email") == true -> "Email no registrado"
                    errorBody?.contains("password") == true -> "Contraseña incorrecta"
                    else -> "Credenciales incorrectas"
                }
                Result.failure(Exception(message))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }
    
    suspend fun register(username: String, email: String, password: String): Result<AuthResponse> {
        return try {
            val response = api.register(RegisterRequest(username, email, password))
            if (response.isSuccessful) {
                val authResponse = response.body()!!
                sessionManager.saveSession(authResponse.token, authResponse.user)
                Result.success(authResponse)
            } else {
                val errorBody = response.errorBody()?.string()
                val message = when {
                    errorBody?.contains("username") == true -> "Nombre de usuario en uso"
                    errorBody?.contains("email") == true -> "Email ya registrado"
                    else -> "Error al crear la cuenta"
                }
                Result.failure(Exception(message))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión"))
        }
    }
    
    suspend fun checkUsername(username: String): Result<Boolean> {
        return try {
            val response = api.checkUsername(username)
            if (response.isSuccessful) {
                Result.success(response.body()?.get("available") ?: false)
            } else {
                Result.success(false)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun isLoggedIn(): Boolean = sessionManager.isLoggedIn()
    fun getUser(): User? = sessionManager.getUser()
    fun logout() = sessionManager.logout()
}
