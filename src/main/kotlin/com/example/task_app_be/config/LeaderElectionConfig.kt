package com.example.task_app_be.config
import io.fabric8.kubernetes.api.model.Pod
import io.fabric8.kubernetes.client.KubernetesClient
import io.fabric8.kubernetes.client.KubernetesClientBuilder
import io.fabric8.kubernetes.client.KubernetesClientException
import io.fabric8.kubernetes.client.extended.leaderelection.LeaderCallbacks
import io.fabric8.kubernetes.client.extended.leaderelection.LeaderElectionConfigBuilder
import io.fabric8.kubernetes.client.extended.leaderelection.resourcelock.LeaseLock
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import java.time.Duration
import java.util.*


private const val NAMESPACE = "default"
private const val NAME = "leaders-of-the-future"
@Configuration
class LeaderElectionConfig {

    @PostConstruct
    fun startLeaderElection() {
        val lockIdentity = UUID.randomUUID().toString()
        val kc = KubernetesClientBuilder().build()

        val leaderCallBacks = LeaderCallbacks(
            {
                ConfigProperties.isLeader = true
                println("STARTED LEADERSHIP")
                ConfigProperties.leaderEndpoint = "http://localhost:9090/api" //should be the current url
            },
            {
                ConfigProperties.isLeader = false
                ConfigProperties.leaderEndpoint = "deneme"
                println("STOPPED LEADERSHIP")
                // Your logic for handling leadership stop
            },
            { newLeader ->
                println("New leader elected $newLeader")
                ConfigProperties.isLeader = false
                ConfigProperties.leaderEndpoint = "http://localhost:9090/api" // should be the leaders url
            }
        )

        val leaderElectionConfigBuilder = LeaderElectionConfigBuilder()
            .withReleaseOnCancel()
            .withName("Sample Leader Election configuration")
            .withLeaseDuration(Duration.ofSeconds(5))
            .withLock(LeaseLock(NAMESPACE, NAME, lockIdentity))
            .withRenewDeadline(Duration.ofSeconds(3))
            .withRetryPeriod(Duration.ofSeconds(1))
            .withLeaderCallbacks(leaderCallBacks)
            .build()

        val leader = kc.leaderElector().withConfig(leaderElectionConfigBuilder).build()
        val f = leader.start()
    }
}