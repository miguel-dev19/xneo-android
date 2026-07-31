package com.xneo.app.ui.screens.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.xneo.app.data.model.User
import com.xneo.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AuthRepository(application)
    
    private val _loginState = MutableStateFlow<AuthState>(AuthState.Idle)
    val loginState: StateFlow<AuthState> = _loginState
    
    private val _registerState = MutableStateFlow<AuthState>(AuthState.Idle)
    val registerState: StateFlow<AuthState> = _registerState
    
    private val _usernameAvailable = MutableStateFlow<UsernameState>(UsernameState.Idle)
    val usernameAvailable: StateFlow<UsernameState> = _usernameAvailable
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = AuthState.Loading
            repository.login(email, password)
                .onSuccess { _loginState.value = AuthState.Success(it.user) }
                .onFailure { _loginState.value = AuthState.Error(it.message ?: "Error") }
        }
    }
    
    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            _registerState.value = AuthState.Loading
            repository.register(username, email, password)
                .onSuccess { _registerState.value = AuthState.Success(it.user) }
                .onFailure { _registerState.value = AuthState.Error(it.message ?: "Error") }
        }
    }
    
    fun checkUsername(username: String) {
        if (username.length < 3) {
            _usernameAvailable.value = UsernameState.TooShort
            return
        }
        viewModelScope.launch {
            _usernameAvailable.value = UsernameState.Checking
            repository.checkUsername(username)
                .onSuccess { available ->
                    _usernameAvailable.value = if (available) UsernameState.Available 
                                               else UsernameState.Unavailable
                }
                .onFailure { _usernameAvailable.value = UsernameState.Error }
        }
    }
    
    fun resetLoginState() { _loginState.value = AuthState.Idle }
    fun resetRegisterState() { _registerState.value = AuthState.Idle }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}

sealed class UsernameState {
    object Idle : UsernameState()
    object Checking : UsernameState()
    object Available : UsernameState()
    object Unavailable : UsernameState()
    object TooShort : UsernameState()
    object Error : UsernameState()
}
