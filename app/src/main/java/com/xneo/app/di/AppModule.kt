package com.xneo.app.di

import android.content.Context
import com.xneo.app.data.local.SessionManager
import com.xneo.app.data.remote.RetrofitClient
import com.xneo.app.data.remote.XNeoApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideSessionManager(@ApplicationContext context: Context): SessionManager {
        return SessionManager(context)
    }
    
    @Provides
    @Singleton
    fun provideXNeoApi(): XNeoApi {
        return RetrofitClient.instance
    }
}
