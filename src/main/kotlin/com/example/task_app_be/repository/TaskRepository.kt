package com.example.task_app_be.repository

import com.example.task_app_be.data.Task
import org.springframework.context.annotation.Description
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface TaskRepository: JpaRepository<Task,Long> {
    fun findTaskById(id:Long) : Task

    fun findFirstByOrderByCreatedOnDesc(): Task

    fun findByTaskOpenIs(boolean: Boolean): List<Task>

    fun findByDescription(description: String): Task

}