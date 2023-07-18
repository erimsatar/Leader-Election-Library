package com.example.task_app_be

import com.example.task_app_be.config.ConfigProperties
import io.kubernetes.client.extended.leaderelection.LeaderElectionConfig
import io.kubernetes.client.extended.leaderelection.LeaderElector
import io.kubernetes.client.extended.leaderelection.resourcelock.EndpointsLock
import io.kubernetes.client.extended.leaderelection.resourcelock.LeaseLock
import io.kubernetes.client.util.ClientBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import java.time.Duration
import java.util.*


@SpringBootApplication
//@EnableConfigurationProperties(ConfigProperties::class)
class TaskAppBeApplication

fun main(args: Array<String>) {
	runApplication<TaskAppBeApplication>(*args)
	println(ConfigProperties.isLeader);
	val apiClient = ClientBuilder.standard().build()
	val leaseLock = LeaseLock("default", "operator", UUID.randomUUID().toString(), apiClient)
	val leaderElectionConfig = LeaderElectionConfig(leaseLock, Duration.ofMillis(2000), Duration.ofMillis(1500), Duration.ofMillis(300))
	val leaderElector = LeaderElector(leaderElectionConfig)

	leaderElector.run(
		{ println("Do something when getting leadership.")
			ConfigProperties.isLeader = true
		}
	) {
		println("Do something when losing leadership.")
		ConfigProperties.isLeader = false
		}
}
