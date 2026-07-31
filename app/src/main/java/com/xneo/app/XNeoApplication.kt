package com.xneo.app

import android.app.Application
import com.xneo.app.data.remote.RetrofitClient

class XNeoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RetrofitClient.initialize(this)
    }
}
EOF && \
cat > app/src/main/java/com/xneo/app/data/remote/interceptor/AuthInterceptor.kt << 'EOF'
package com.xneo.app.data.remote.interceptor

import android.content.Context
import com.xneo.app.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(context: Context) : Interceptor {
    private val sessionManager = SessionManager(context)
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = sessionManager.getToken()
        
        val request = if (token != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }
        
        return chain.proceed(request)
    }
}
EOF && \
cat > app/src/main/java/com/xneo/app/data/remote/RetrofitClient.kt << 'EOF'
package com.xneo.app.data.remote

import android.content.Context
import com.xneo.app.data.remote.interceptor.AuthInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://xneo-api.onrender.com/"
    
    private var authInterceptor: AuthInterceptor? = null
    
    fun initialize(context: Context) {
        authInterceptor = AuthInterceptor(context.applicationContext)
    }
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private fun getOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
        
        authInterceptor?.let { builder.addInterceptor(it) }
        
        return builder.build()
    }
    
    val instance: XNeoApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(XNeoApi::class.java)
    }
}
EOF && \
cat > app/src/main/AndroidManifest.xml << 'EOF'
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    
    <application
        android:name=".XNeoApplication"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="XNEO"
        android:supportsRtl="true"
        android:theme="@style/Theme.XNEO"
        android:usesCleartextTraffic="true">
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:theme="@style/Theme.XNEO">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
EOF && \
cat > app/src/main/java/com/xneo/app/data/repository/AuthRepository.kt << 'EOF'
package com.xneo.app.data.repository

import android.content.Context
import com.xneo.app.data.local.SessionManager
import com.xneo.app.data.model.*
import com.xneo.app.data.remote.RetrofitClient

class AuthRepository(context: Context) {
    private val api = RetrofitClient.instance
    private val sessionManager = SessionManager(context)
    
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
EOF && \
cat > app/src/main/java/com/xneo/app/ui/screens/home/HomeViewModel.kt << 'EOF'
package com.xneo.app.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.xneo.app.data.model.Video
import com.xneo.app.data.repository.AuthRepository
import com.xneo.app.data.repository.VideoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val videoRepository = VideoRepository()
    private val authRepository = AuthRepository(application)
    
    private val _videos = MutableStateFlow<List<Video>>(emptyList())
    val videos: StateFlow<List<Video>> = _videos
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    val isLoggedIn: Boolean
        get() = authRepository.isLoggedIn()
    
    init { loadVideos() }

    fun loadVideos(category: String = "hetero") {
        viewModelScope.launch {
            _isLoading.value = true
            videoRepository.getVideosByCategory(category).onSuccess { 
                _videos.value = it.videos 
            }
            _isLoading.value = false
        }
    }
    
    fun logout(navController: NavController) {
        authRepository.logout()
        navController.navigate("login") {
            popUpTo("home") { inclusive = true }
        }
    }
}
EOF && \
cat > app/src/main/java/com/xneo/app/ui/screens/player/PlayerViewModel.kt << 'EOF'
package com.xneo.app.ui.screens.player

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.xneo.app.data.model.Video
import com.xneo.app.data.repository.AuthRepository
import com.xneo.app.data.repository.VideoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = VideoRepository()
    private val authRepository = AuthRepository(application)
    
    private val _video = MutableStateFlow<Video?>(null)
    val video: StateFlow<Video?> = _video
    
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    val isLoggedIn: Boolean
        get() = authRepository.isLoggedIn()

    fun loadVideo(videoId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getVideoById(videoId).onSuccess { 
                _video.value = it 
            }
            _isLoading.value = false
        }
    }
}
