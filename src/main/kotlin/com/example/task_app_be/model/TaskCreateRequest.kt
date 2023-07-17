package com.example.task_app_be.model

import com.example.task_app_be.data.Priority
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime


data class TaskCreateRequest(
    val description: String,
    val isReminderSet: Boolean,
    val isTaskOpen: Boolean,
    val priority: Priority
)
