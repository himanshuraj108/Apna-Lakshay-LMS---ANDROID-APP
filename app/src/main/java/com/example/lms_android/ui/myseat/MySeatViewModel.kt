package com.example.lms_android.ui.myseat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lms_android.data.api.ApiClient
import com.example.lms_android.data.models.MySeatResponse
import com.example.lms_android.data.models.ShiftResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class MySeatState {
    object Loading : MySeatState()
    data class Success(
        val mySeatData: MySeatResponse,
        val availableShifts: List<ShiftResponse>
    ) : MySeatState()
    data class Error(val message: String) : MySeatState()
}

class MySeatViewModel : ViewModel() {
    private val _state = MutableStateFlow<MySeatState>(MySeatState.Loading)
    val state: StateFlow<MySeatState> = _state.asStateFlow()

    init {
        fetchMySeat()
    }

    fun fetchMySeat() {
        viewModelScope.launch {
            _state.value = MySeatState.Loading
            try {
                // Fetch seat data and available shifts in parallel
                val seatDeferred = ApiClient.apiService.getMySeat()
                val shiftsDeferred = ApiClient.apiService.getPublicShifts()

                if (seatDeferred.success && shiftsDeferred.success) {
                    _state.value = MySeatState.Success(
                        mySeatData = seatDeferred,
                        availableShifts = shiftsDeferred.shifts ?: emptyList()
                    )
                } else {
                    _state.value = MySeatState.Error("Failed to fetch seat details.")
                }
            } catch (e: Exception) {
                _state.value = MySeatState.Error(e.localizedMessage ?: "Network error occurred")
            }
        }
    }
}
