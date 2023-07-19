package com.example.task_app_be.leader_election

import io.fabric8.kubernetes.client.KubernetesClient
import io.fabric8.kubernetes.client.extended.leaderelection.LeaderElectionConfig
import io.fabric8.kubernetes.client.extended.leaderelection.LeaderElector
import io.fabric8.kubernetes.client.extended.leaderelection.LeaderElectorBuilder
import java.util.concurrent.Executor

class CustomLeaderElectorBuilder(
    private val client: KubernetesClient,
    private val executor: Executor
) : LeaderElectorBuilder(client, executor) {
    private var leaderElectionConfig: LeaderElectionConfig? = null
    override fun withConfig(leaderElectionConfig: LeaderElectionConfig): LeaderElectorBuilder {
        this.leaderElectionConfig = leaderElectionConfig
        return this
    }

    override fun build(): LeaderElector {
        return LeaderElector(client, leaderElectionConfig, executor)
    }
}
