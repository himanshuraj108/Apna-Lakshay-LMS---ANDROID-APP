package com.example.lms_android.data.models

data class TaskResponse(
    val success: Boolean,
    val tasks: List<StudyTask>?
)

data class SingleTaskResponse(
    val success: Boolean,
    val task: StudyTask?
)

data class StudyTask(
    val _id: String,
    val title: String,
    val description: String? = null,
    val priority: String = "Medium Priority", // e.g., "High", "Medium Priority"
    val estimatedTime: Int = 30, // in minutes
    val dueDate: String? = null, // ISO string
    val completed: Boolean = false,
    val pomodoroSessions: Int = 0,
    val totalFocusTime: Int = 0
)

data class CreateTaskRequest(
    val title: String,
    val description: String? = null,
    val priority: String = "Medium Priority",
    val estimatedTime: Int = 30,
    val dueDate: String? = null
)

data class UpdateTaskRequest(
    val completed: Boolean
)

data class ExamResponse(
    val success: Boolean,
    val exams: List<Exam>?
)

data class SingleExamResponse(
    val success: Boolean,
    val exam: Exam?
)

data class Exam(
    val _id: String,
    val title: String,
    val date: String,
    val subject: String? = null,
    val colorTag: String? = "BLUE"
)

data class CreateExamRequest(
    val title: String,
    val date: String,
    val subject: String? = null,
    val colorTag: String? = "BLUE"
)

data class LogPomodoroRequest(
    val taskId: String? = null,
    val duration: Int, // in minutes
    val type: String // "focus", "short_break", "long_break"
)

data class BaseResponse(
    val success: Boolean,
    val message: String?
)

data class StatsResponse(
    val success: Boolean,
    val stats: StudyStats?
)

data class StudyStats(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalFocusTime: Int = 0,
    val totalXP: Int = 0,
    val tasksCompleted: Int = 0,
    val level: Int = 1,
    val activityLog: List<ActivityLogEntry> = emptyList()
)

data class ActivityLogEntry(
    val date: String,
    val count: Int
)
