package com.example.lms_android.ui.fee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lms_android.data.api.ApiClient
import com.example.lms_android.data.models.FeeRecord
import com.example.lms_android.data.models.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class FeeState {
    object Loading : FeeState()
    data class Success(
        val fees: List<FeeRecord>,
        val profile: UserProfile
    ) : FeeState()
    data class Error(val message: String) : FeeState()
}

class FeeViewModel : ViewModel() {
    private val _state = MutableStateFlow<FeeState>(FeeState.Loading)
    val state: StateFlow<FeeState> = _state.asStateFlow()

    init { fetchFees() }

    fun fetchFees() {
        viewModelScope.launch {
            _state.value = FeeState.Loading
            try {
                val feesRes = ApiClient.apiService.getFees()
                val profileRes = ApiClient.apiService.getProfile()
                if (feesRes.success && profileRes.success) {
                    _state.value = FeeState.Success(
                        fees = feesRes.fees ?: emptyList(),
                        profile = profileRes.user ?: UserProfile(null, null, null, null, null, null, null, null)
                    )
                } else {
                    _state.value = FeeState.Error("Failed to load fee data")
                }
            } catch (e: Exception) {
                _state.value = FeeState.Error(e.localizedMessage ?: "Network error")
            }
        }
    }
}
