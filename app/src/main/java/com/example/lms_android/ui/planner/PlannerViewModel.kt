package com.example.lms_android.ui.planner

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lms_android.data.api.ApiClient
import com.example.lms_android.data.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class PlannerState {
    object Loading : PlannerState()
    data class Success(
        val tasks: List<StudyTask>,
        val exams: List<Exam>,
        val stats: StudyStats
    ) : PlannerState()
    data class Error(val message: String) : PlannerState()
}

class PlannerViewModel(application: Application) : AndroidViewModel(application) {

    private val api = ApiClient.apiService

    private val _plannerState = MutableStateFlow<PlannerState>(PlannerState.Loading)
    val plannerState: StateFlow<PlannerState> = _plannerState

    init {
        fetchPlannerData()
    }

    fun fetchPlannerData() {
        viewModelScope.launch {
            _plannerState.value = PlannerState.Loading
            try {
                // In production, these should run in parallel via async/await
                val tasksRes = api.getTasks()
                val examsRes = api.getExams()
                var statsRes: StatsResponse? = null
                try {
                    statsRes = api.getStats()
                } catch (e: Exception) {
                    Log.e("PlannerVM", "Stats fetch failed (maybe no stats yet)", e)
                }

                if (tasksRes.success && examsRes.success) {
                    _plannerState.value = PlannerState.Success(
                        tasks = tasksRes.tasks ?: emptyList(),
                        exams = examsRes.exams ?: emptyList(),
                        stats = statsRes?.stats ?: StudyStats()
                    )
                } else {
                    _plannerState.value = PlannerState.Error("Failed to fetch planner data")
                }
            } catch (e: Exception) {
                _plannerState.value = PlannerState.Error(e.message ?: "Network error")
            }
        }
    }

    fun createTask(request: CreateTaskRequest) {
        viewModelScope.launch {
            try {
                val response = api.createTask(request)
                if (response.success && response.task != null) {
                    val currentState = _plannerState.value
                    if (currentState is PlannerState.Success) {
                        _plannerState.value = currentState.copy(
                            tasks = listOf(response.task) + currentState.tasks
                        )
                    } else {
                        fetchPlannerData()
                    }
                }
            } catch (e: Exception) {
                Log.e("PlannerVM", "Create task failed", e)
            }
        }
    }

    fun toggleTaskCompletion(taskId: String, completed: Boolean) {
        viewModelScope.launch {
            try {
                val response = api.updateTask(taskId, UpdateTaskRequest(completed))
                if (response.success && response.task != null) {
                    // Updating stats requires a refetch
                    fetchPlannerData()
                }
            } catch (e: Exception) {
                Log.e("PlannerVM", "Update task failed", e)
            }
        }
    }

    fun createExam(request: CreateExamRequest) {
        viewModelScope.launch {
            try {
                val response = api.createExam(request)
                if (response.success && response.exam != null) {
                    val currentState = _plannerState.value
                    if (currentState is PlannerState.Success) {
                        _plannerState.value = currentState.copy(
                            exams = (currentState.exams + response.exam).sortedBy { it.date }
                        )
                    } else {
                        fetchPlannerData()
                    }
                }
            } catch (e: Exception) {
                Log.e("PlannerVM", "Create exam failed", e)
            }
        }
    }

    fun deleteExam(examId: String) {
        viewModelScope.launch {
            try {
                val response = api.deleteExam(examId)
                if (response.success) {
                    val currentState = _plannerState.value
                    if (currentState is PlannerState.Success) {
                        _plannerState.value = currentState.copy(
                            exams = currentState.exams.filter { it._id != examId }
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("PlannerVM", "Delete exam failed", e)
            }
        }
    }

    fun logPomodoroSession(taskId: String?, duration: Int, type: String) {
        viewModelScope.launch {
            try {
                val response = api.logPomodoro(LogPomodoroRequest(taskId, duration, type))
                if (response.success) {
                    // Refetch stats/tasks to get updated XP/focus time
                    fetchPlannerData()
                }
            } catch (e: Exception) {
                Log.e("PlannerVM", "Log pomodoro failed", e)
            }
        }
    }
}
