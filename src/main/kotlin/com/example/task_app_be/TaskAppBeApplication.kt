package com.example.task_app_be

import io.kubernetes.client.extended.leaderelection.LeaderElectionConfig
import io.kubernetes.client.extended.leaderelection.LeaderElector
import io.kubernetes.client.extended.leaderelection.resourcelock.EndpointsLock
import io.kubernetes.client.extended.leaderelection.resourcelock.LeaseLock
import io.kubernetes.client.util.ClientBuilder
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.time.Duration
import java.util.*


@SpringBootApplication
class TaskAppBeApplication

fun main(args: Array<String>) {
	runApplication<TaskAppBeApplication>(*args)
	println("Just checking");
	val apiClient = ClientBuilder.standard().build()
	val leaseLock = LeaseLock("default", "operator", UUID.randomUUID().toString(), apiClient)
	val leaderElectionConfig = LeaderElectionConfig(leaseLock, Duration.ofMillis(10000), Duration.ofMillis(5000), Duration.ofMillis(3000))
	val leaderElector = LeaderElector(leaderElectionConfig)

	leaderElector.run(
		{ println("Do something when getting leadership.") }
	) { println("Do something when losing leadership.") }
}
