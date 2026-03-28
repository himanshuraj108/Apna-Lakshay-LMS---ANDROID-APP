package com.example.lms_android.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lms_android.data.api.ApiClient
import com.example.lms_android.data.models.DashboardData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class HomeState {
    object Loading : HomeState()
    data class Success(val dashboard: DashboardData) : HomeState()
    data class Error(val message: String) : HomeState()
}

class HomeViewModel : ViewModel() {
    private val _homeState = MutableStateFlow<HomeState>(HomeState.Loading)
    val homeState: StateFlow<HomeState> = _homeState.asStateFlow()

    init {
        fetchDashboard()
    }

    fun fetchDashboard() {
        _homeState.value = HomeState.Loading
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getDashboard()
                if (response.success && response.data != null) {
                    _homeState.value = HomeState.Success(response.data)
                } else {
                    _homeState.value = HomeState.Error("Failed to load dashboard data.")
                }
            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val message = errorBody?.takeIf { it.contains("\"message\"") }
                    ?.substringAfter("\"message\":\"")?.substringBefore("\"")
                    ?: "Server Error: ${e.code()}"
                
                if (e.code() == 401) {
                     _homeState.value = HomeState.Error("Session Expired. Please restart the app and login again.")
                } else {
                     _homeState.value = HomeState.Error(message)
                }
            } catch (e: Exception) {
                _homeState.value = HomeState.Error(e.localizedMessage ?: "Network error. Is your server running?")
            }
        }
    }
}
