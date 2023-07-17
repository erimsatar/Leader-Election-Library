package com.example.task_app_be.service

import com.example.task_app_be.data.Task
import com.example.task_app_be.exception.TaskNotFoundException
import com.example.task_app_be.model.TaskCreateRequest
import com.example.task_app_be.model.TaskUpdateRequest
import com.example.task_app_be.repository.TaskRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.stream.Collectors

@Service
class TaskService(private val repository: TaskRepository) {

    private fun checkForTaskId(id:Long){
        if(!repository.existsById(id)){
            throw TaskNotFoundException("Task with ID: $id does not exist!")
        }
    }

    fun getAllTasks(): List<Task> =
        repository.findAll().stream().collect(Collectors.toList())

    fun getAllOpenTasks(): List<Task> =
        repository.queryAllOpenTasks().stream().collect(Collectors.toList())

    fun getAllClosedTasks(): List<Task> =
        repository.queryAllClosedTasks().stream().collect(Collectors.toList())

    fun getTaskById(id: Long): Task {
        checkForTaskId(id)
        return repository.findTaskById(id)
    }
    fun createTask(createRequest: TaskCreateRequest): Task =
        try {
             repository.save(
                Task(
                    description = createRequest.description,
                    isReminderSet = createRequest.isReminderSet,
                    isTaskOpen = createRequest.isTaskOpen,
                    priority = createRequest.priority,
                    createdOn = LocalDateTime.now()
                )
            )
        } catch (e: Throwable){
            throw(e)
        }


    fun updateTask(updateRequest: TaskUpdateRequest, id:Long):Task{
        val entity = repository.findTaskById(id)

        return repository.save(
            Task(
                id = id,
                description = updateRequest.description?: entity.description,
                isReminderSet = updateRequest.isReminderSet ?: entity.isReminderSet,
                isTaskOpen = updateRequest.isTaskOpen ?: entity.isTaskOpen,
                priority = updateRequest.priority ?: entity.priority,
            )
        )
    }

    fun deleteTask(id: Long): String {
        checkForTaskId(id)
        repository.deleteById(id)
        return "Task with id: $id has been deleted."
    }

}