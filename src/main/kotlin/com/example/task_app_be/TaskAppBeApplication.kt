package com.example.task_app_be

import com.example.task_app_be.config.ConfigProperties
import io.fabric8.kubernetes.client.KubernetesClientBuilder
import io.fabric8.kubernetes.client.extended.leaderelection.LeaderCallbacks
import io.fabric8.kubernetes.client.extended.leaderelection.LeaderElectionConfigBuilder
import io.fabric8.kubernetes.client.extended.leaderelection.resourcelock.LeaseLock
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.time.Duration
import java.util.*


@SpringBootApplication
//@EnableConfigurationProperties(ConfigProperties::class)
class TaskAppBeApplication

private const val NAMESPACE = "default"
private const val NAME = "leaders-of-the-future"

fun main(args: Array<String>) {
	/*
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

	 */
	runApplication<TaskAppBeApplication>(*args)
	val lockIdentity = UUID.randomUUID().toString()
	val kc = KubernetesClientBuilder().build()
	val leaderCallBacks = LeaderCallbacks(
		{ConfigProperties.isLeader = true
			println("STARTED LEADERSHIP")
		},
		{ConfigProperties.isLeader = false
			println("STOPPED LEADERSHIP")},
		{
			newLeader -> println("New leader elected $newLeader")
			ConfigProperties.isLeader = false
		}
	)
	val leaderElectionConfigBuilder = LeaderElectionConfigBuilder()
		.withReleaseOnCancel()
		.withName("Sample Leader Election configuration")
		.withLeaseDuration(Duration.ofSeconds(5))
		.withLock( LeaseLock(NAMESPACE, NAME, lockIdentity))
		.withRenewDeadline(Duration.ofSeconds(3))
		.withRetryPeriod(Duration.ofSeconds(1))
		.withLeaderCallbacks(leaderCallBacks)
		.build()
	val leader = kc.leaderElector().withConfig(leaderElectionConfigBuilder).build()
	val f = leader.start()
	Thread.sleep(15000)
	f.cancel(true)
}
