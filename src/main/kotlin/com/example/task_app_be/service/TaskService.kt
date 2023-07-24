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
class TaskService(private val repository: TaskRepository) {
    var webClient = WebClient.create()

    private fun checkForTaskId(id: Long) {
        if (!repository.existsById(id)) {
            throw TaskNotFoundException("Task with ID: $id does not exist!")
        }
    }

    fun getAllTasks(): Any? {
        if (ConfigProperties.isLeader) {
            return repository.findAll()
        } else {
            return getRedirectNoParam("all-tasks")
        }
    }

    fun getAllOpenTasks(): Any? { //this doesnt work.
        if (ConfigProperties.isLeader) {
            return repository.findByTaskOpenIs(true)
        } else {
            return getRedirectNoParam("all-tasks")
        }
    }

    fun getAllClosedTasks(): Any? { //this doesnt work.
        if (ConfigProperties.isLeader) {
            return repository.findByTaskOpenIs(false)
        } else {
            return getRedirectNoParam("all-tasks")
        }
    }

    fun getTaskById(id: Long): Any? {
        if (ConfigProperties.isLeader) {
            checkForTaskId(id)
            return repository.findTaskById(id)
        } else {
            return getRedirectNoParam("task/$id")
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
            val urlModifyingFilter = createUrlModifyingFilter("create")
            webClient = createWebClient(urlModifyingFilter)
            webClient.post()
                .bodyValue(createRequest)
                .retrieve()
                .bodyToMono(Any::class.java)
        }
    }


    fun updateTask(updateRequest: TaskUpdateRequest, id: Long): Any {
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
            val urlModifyingFilter = createUrlModifyingFilter("update/$id")
            webClient = createWebClient(urlModifyingFilter)
            return webClient.post()
                .bodyValue(updateRequest)
                .retrieve()
                .bodyToMono(Any::class.java)
        }
    }


    fun deleteTask(id: Long): Any { //this doesnt work delete request needed.
        if (ConfigProperties.isLeader) {
            checkForTaskId(id)
            repository.deleteById(id)
            return "Task with id: $id has been deleted."
        } else {
            val urlModifyingFilter = createUrlModifyingFilter("delete/$id")
            webClient = createWebClient(urlModifyingFilter)
            return webClient.delete()
                .retrieve().bodyToMono(Any::class.java)
        }
    }

    fun getLastTask(): Any {
        if (ConfigProperties.isLeader) {
            return repository.findFirstByOrderByCreatedOnDesc()
        } else {
            return getRedirectNoParam("last-task")
        }

    }

    fun getRedirectNoParam(request: String): Any {
        val urlModifyingFilter = createUrlModifyingFilter(request)
        webClient = createWebClient(urlModifyingFilter)
        return webClient.get()
            .retrieve().bodyToMono(Any::class.java)
    }


    fun createUrlModifyingFilter(request: String): ExchangeFilterFunction {
        return ExchangeFilterFunction { clientRequest, nextFilter ->
            val oldUrl = ConfigProperties.leaderEndpoint
            val newUrl = URI.create("$oldUrl/$request")
            val filteredRequest = ClientRequest.from(clientRequest)
                .url(newUrl)
                .build()
            nextFilter.exchange(filteredRequest)
        }
    }

    fun createWebClient(urlModifyingFilter: ExchangeFilterFunction): WebClient =
        WebClient.builder().filter(urlModifyingFilter).build()
}