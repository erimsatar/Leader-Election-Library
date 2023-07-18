package com.example.task_app_be.controller

import com.example.task_app_be.config.ConfigProperties
import com.example.task_app_be.data.Task
import com.example.task_app_be.model.TaskCreateRequest
import com.example.task_app_be.model.TaskUpdateRequest
import com.example.task_app_be.service.TaskService
import jakarta.validation.Valid
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("api")
class TaskController(private val service: TaskService) {

    @GetMapping("all-tasks")
    fun getAllTasks(): List<Task> {
        if (!ConfigProperties.isLeader) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Resource not found")
        }
        return service.getAllTasks()
    }

    @GetMapping("open-tasks")
    fun getAllOpenTasks(): List<Task> = service.getAllOpenTasks()

    @GetMapping("closed-tasks")
    fun getAllClosedTasks(): List<Task> = service.getAllClosedTasks()

    @GetMapping("task/{id}")
    fun getTaskById(@PathVariable id: Long): Task =
        service.getTaskById(id)

    @PostMapping("create")
    fun createTask(
        @Valid @RequestBody createRequest: TaskCreateRequest
    ): Task = service.createTask(createRequest)


    @PostMapping("update/{id}")
    fun updateTask(
        @Valid @RequestBody updateRequest: TaskUpdateRequest,
        @PathVariable id: Long
    ): Task = service.updateTask(updateRequest,id)


    @DeleteMapping("delete/{id}")
    fun deleteTask(@PathVariable id: Long): ResponseEntity<String> =
        ResponseEntity(service.deleteTask(id), HttpStatus.OK)

}