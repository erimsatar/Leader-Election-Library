package com.example.task_app_be.data

import jakarta.persistence.Entity
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime


@Entity
@Table(name = "task", uniqueConstraints = [UniqueConstraint(name = "uk_task_description", columnNames = ["description"])])
data class Task(
    @Id
    @GeneratedValue(generator = "task_sequence", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "task_sequence", sequenceName = "task_sequence", allocationSize = 1)
    var id: Long = 0,

    @NotBlank
    @Column(name = "description", nullable = false, unique = true)
    var description: String = "",

    @Column(name= "is_reminder_set", nullable = false)
    var isReminderSet: Boolean = false,

    @Column(name = "is_task_open", nullable = false)
    var taskOpen: Boolean = true,

    @Column(name = "created_on", nullable = false)
    var createdOn: LocalDateTime = LocalDateTime.now(),

    @NotNull
    @Enumerated(EnumType.STRING)
    var priority: Priority = Priority.LOW,
)