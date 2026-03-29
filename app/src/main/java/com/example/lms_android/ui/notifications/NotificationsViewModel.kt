package com.example.lms_android.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lms_android.data.api.ApiClient
import com.example.lms_android.data.models.NotificationModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class NotificationsState {
    object Loading : NotificationsState()
    data class Success(val notifications: List<NotificationModel>) : NotificationsState()
    data class Error(val message: String) : NotificationsState()
}

class NotificationsViewModel : ViewModel() {
    private val _state = MutableStateFlow<NotificationsState>(NotificationsState.Loading)
    val state: StateFlow<NotificationsState> = _state.asStateFlow()

    init {
        fetchNotifications()
    }

    fun fetchNotifications() {
        viewModelScope.launch {
            _state.value = NotificationsState.Loading
            try {
                val response = ApiClient.apiService.getNotifications()
                if (response.success) {
                    _state.value = NotificationsState.Success(response.notifications)
                } else {
                    _state.value = NotificationsState.Error("Failed to fetch notifications")
                }
            } catch (e: Exception) {
                _state.value = NotificationsState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun markAsRead(id: String) {
        viewModelScope.launch {
            try {
                // Optimistic update
                val currentState = _state.value
                if (currentState is NotificationsState.Success) {
                    val updatedList = currentState.notifications.map {
                        if (it._id == id) it.copy(isRead = true) else it
                    }
                    _state.value = NotificationsState.Success(updatedList)
                }
                // API call
                ApiClient.apiService.markNotificationRead(id)
            } catch (e: Exception) {
                // Revert or refetch on silently failed API
            }
        }
    }
}
