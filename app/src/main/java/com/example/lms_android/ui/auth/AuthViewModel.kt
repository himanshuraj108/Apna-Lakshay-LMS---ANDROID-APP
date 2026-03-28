package com.example.lms_android.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lms_android.data.api.ApiClient
import com.example.lms_android.data.models.LoginRequest
import com.example.lms_android.data.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User, val token: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Please enter both email and password")
            return
        }
        
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val request = LoginRequest(email, password)
                val response = ApiClient.apiService.login(request)
                if (response.success && response.user != null && response.token != null) {
                    com.example.lms_android.data.TokenManager.saveToken(response.token, response.user.name)
                    _authState.value = AuthState.Success(response.user, response.token)
                } else {
                    _authState.value = AuthState.Error(response.message ?: "Invalid credentials")
                }
            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val message = errorBody?.takeIf { it.contains("\"message\"") }
                    ?.substringAfter("\"message\":\"")?.substringBefore("\"")
                    ?: "Server Error: ${e.code()}"
                _authState.value = AuthState.Error(message)
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Network Error. Is your server running?")
            }
        }
    }
}
