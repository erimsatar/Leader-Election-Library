package com.example.task_app_be.config

import java.util.concurrent.atomic.AtomicBoolean


class ConfigProperties {
    companion object {
        var isLeader: Boolean = false
        var leaderEndpoint:String? = null;
    }
}