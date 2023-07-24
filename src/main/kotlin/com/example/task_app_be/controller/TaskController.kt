package com.example.task_app_be.controller

import com.example.task_app_be.model.TaskCreateRequest
import com.example.task_app_be.model.TaskUpdateRequest
import com.example.task_app_be.service.TaskService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.client.WebClient



@RestController
@RequestMapping("api")
class TaskController(
    private val service: TaskService,
) {
    var webClient = WebClient.create()

    @GetMapping("all-tasks")
    fun getAllTasks(): Any? = service.getAllTasks()


    @GetMapping("last-task")
    fun getLastTask(): Any? =service.getLastTask()


    @GetMapping("open-tasks")
    fun getAllOpenTasks(): Any? = service.getAllOpenTasks()

    @GetMapping("closed-tasks")
    fun getAllClosedTasks(): Any? = service.getAllClosedTasks()


    @GetMapping("task/{id}")
    fun getTaskById(@PathVariable id: Long): Any? = service.getTaskById(id)


    @PostMapping("create")
    fun createTask(
        @Valid @RequestBody createRequest: TaskCreateRequest
    ): Any? = service.createTask(createRequest)


    @PostMapping("update/{id}")
    fun updateTask(
        @Valid @RequestBody updateRequest: TaskUpdateRequest,
        @PathVariable id: Long
    ): Any? = service.updateTask(updateRequest, id)


    @DeleteMapping("delete/{id}")
    fun deleteTask(@PathVariable id: Long): Any? =  service.deleteTask(id)




}