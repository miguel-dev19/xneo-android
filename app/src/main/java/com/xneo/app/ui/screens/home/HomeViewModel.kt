package com.xneo.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xneo.app.data.model.Video
import com.xneo.app.data.repository.VideoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val repository = VideoRepository()
    private val _videos = MutableStateFlow<List<Video>>(emptyList())
    val videos: StateFlow<List<Video>> = _videos
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init { loadVideos() }

    fun loadVideos(category: String = "hetero") {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getVideosByCategory(category).onSuccess { _videos.value = it.videos }
            _isLoading.value = false
        }
    }
}
