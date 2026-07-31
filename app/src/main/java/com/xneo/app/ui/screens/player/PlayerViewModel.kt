package com.xneo.app.ui.screens.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xneo.app.data.model.Video
import com.xneo.app.data.repository.VideoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlayerViewModel : ViewModel() {
    private val repository = VideoRepository()
    private val _video = MutableStateFlow<Video?>(null)
    val video: StateFlow<Video?> = _video
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadVideo(videoId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getVideoById(videoId).onSuccess { _video.value = it }
            _isLoading.value = false
        }
    }
}
