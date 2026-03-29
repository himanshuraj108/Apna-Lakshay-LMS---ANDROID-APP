package com.example.lms_android.ui.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lms_android.data.api.ApiClient
import com.example.lms_android.data.models.AttendanceResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AttendanceState {
    object Loading : AttendanceState()
    data class Success(val data: AttendanceResponse) : AttendanceState()
    data class Error(val message: String) : AttendanceState()
}

class AttendanceViewModel : ViewModel() {
    private val _state = MutableStateFlow<AttendanceState>(AttendanceState.Loading)
    val state: StateFlow<AttendanceState> = _state.asStateFlow()

    init {
        fetchAttendance()
    }

    fun fetchAttendance() {
        _state.value = AttendanceState.Loading
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getAttendance()
                if (response.success) {
                    _state.value = AttendanceState.Success(response)
                } else {
                    _state.value = AttendanceState.Error("Failed to load attendance data.")
                }
            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val message = errorBody?.takeIf { it.contains("\"message\"") }
                    ?.substringAfter("\"message\":\"")?.substringBefore("\"")
                    ?: "Server Error: ${e.code()}"
                _state.value = AttendanceState.Error(message)
            } catch (e: Exception) {
                _state.value = AttendanceState.Error(e.localizedMessage ?: "Network error.")
            }
        }
    }
}
