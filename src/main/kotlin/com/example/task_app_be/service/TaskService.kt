package com.example.task_app_be.service

import com.example.task_app_be.config.ConfigProperties
import com.example.task_app_be.data.Task
import com.example.task_app_be.exception.TaskNotFoundException
import com.example.task_app_be.model.TaskCreateRequest
import com.example.task_app_be.model.TaskUpdateRequest
import com.example.task_app_be.repository.TaskRepository
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.net.URI
import java.time.LocalDateTime
import java.util.stream.Collectors

@Service
class TaskService(private val repository: TaskRepository,private val webClient: WebClient) {

    private fun checkForTaskId(id: Long) {
        if (!repository.existsById(id)) {
            throw TaskNotFoundException("Task with ID: $id does not exist!")
        }
    }

    fun getAllTasks(): Any? {
        if (ConfigProperties.isLeader) {
            return repository.findAll()
        } else {
            ConfigProperties.redirectPath = "all-tasks"
            return webClient.get().retrieve().bodyToMono(Any::class.java)
        }
    }

    fun getAllOpenTasks(): Any? {
        if (ConfigProperties.isLeader) {
            return repository.findByTaskOpenIs(true)
        } else {
            ConfigProperties.redirectPath = "open-tasks"
            return webClient.get().retrieve().bodyToMono(Any::class.java)
        }
    }

    fun getAllClosedTasks(): Any? {
        if (ConfigProperties.isLeader) {
            return repository.findByTaskOpenIs(false)
        } else {
            ConfigProperties.redirectPath = "closed-tasks"
            return webClient.get().retrieve().bodyToMono(Any::class.java)
        }
    }

    fun getTaskById(id: Long): Any? {
        if (ConfigProperties.isLeader) {
            checkForTaskId(id)
            return repository.findTaskById(id)
        } else {
            ConfigProperties.redirectPath = "task/$id"
            return webClient.get().retrieve().bodyToMono(Any::class.java)
        }
    }

    fun createTask(createRequest: TaskCreateRequest): Any? {
        return if (ConfigProperties.isLeader) {
            try {
                repository.save(
                    Task(
                        description = createRequest.description,
                        isReminderSet = createRequest.isReminderSet,
                        taskOpen = createRequest.isTaskOpen,
                        priority = createRequest.priority,
                        createdOn = LocalDateTime.now()
                    )
                )
            } catch (e: Throwable) {
                throw (e)
            }
        } else {
            return webClient.post()
                .bodyValue(createRequest)
                .retrieve()
                .bodyToMono(Any::class.java)
        }
    }


    fun updateTask(updateRequest: TaskUpdateRequest, id: Long): Any? {
        if (ConfigProperties.isLeader) {
            val entity = repository.findTaskById(id)
            return repository.save(
                Task(
                    id = id,
                    description = updateRequest.description ?: entity.description,
                    isReminderSet = updateRequest.isReminderSet ?: entity.isReminderSet,
                    taskOpen = updateRequest.isTaskOpen ?: entity.taskOpen,
                    priority = updateRequest.priority ?: entity.priority,
                )
            )
        } else {
            return webClient.post()
                .bodyValue(updateRequest)
                .retrieve()
                .bodyToMono(Any::class.java)
        }
    }


    fun deleteTask(id: Long): Any? {
        if (ConfigProperties.isLeader) {
            checkForTaskId(id)
            repository.deleteById(id)
            return "Task with id: $id has been deleted."
        } else {
            ConfigProperties.redirectPath = "delete/$id"
            return webClient.delete()
                .retrieve().bodyToMono(Any::class.java)
        }
    }

    fun getLastTask(): Any? {
        if (ConfigProperties.isLeader) {
            return repository.findFirstByOrderByCreatedOnDesc()
        } else {
            ConfigProperties.redirectPath = "last-task"
            return webClient.get().retrieve().bodyToMono(Any::class.java)
        }

    }
}