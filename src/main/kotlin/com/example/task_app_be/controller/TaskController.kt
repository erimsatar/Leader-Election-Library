package com.example.task_app_be.controller

import com.example.task_app_be.config.ConfigProperties
import com.example.task_app_be.data.Task
import com.example.task_app_be.model.TaskCreateRequest
import com.example.task_app_be.model.TaskUpdateRequest
import com.example.task_app_be.service.TaskService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.server.ResponseStatusException


@RestController
@RequestMapping("api")
class TaskController(
    private val service: TaskService,
    private val restTemplate: RestTemplate
) {
    val webClient = WebClient.create()

    @GetMapping("all-tasks")
    fun getAllTasks(): Any? {
        if (ConfigProperties.isLeader) {
            return service.getAllTasks()
        }
        else if(ConfigProperties.leaderEndpoint != null) {
            val leaderEnd = ConfigProperties.leaderEndpoint.plus("all-tasks")
            println("Redirecting to leader.")
            //return restTemplate.getForEntity("http://localhost:9090/api/all-tasks", String::class.java)
            return webClient.get()
                .uri("http://localhost:9090/api/all-tasks")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Array<Task>::class.java)
        }
        else{
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Resource not found")
        }
    }
    @GetMapping("last-task")
    fun getLastTask(): Task{
        return service.getLastTask()
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