package com.example.task_app_be.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import java.net.URI


@Configuration
class WebClientConfig {

    @Bean
    fun createUrlModifyingFilter(): ExchangeFilterFunction {
        return ExchangeFilterFunction { clientRequest, nextFilter ->
            val oldUrl = ConfigProperties.leaderEndpoint
            val newUrl = URI.create("$oldUrl/${ConfigProperties.redirectPath}")
            val filteredRequest = ClientRequest.from(clientRequest)
                .url(newUrl)
                .build()
            nextFilter.exchange(filteredRequest)
        }
    }

    @Bean
    fun createWebClient(urlModifyingFilter: ExchangeFilterFunction): WebClient {
        return WebClient.builder().filter(urlModifyingFilter).build()
    }

}