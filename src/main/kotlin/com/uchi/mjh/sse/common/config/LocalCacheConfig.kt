package com.uchi.mjh.sse.common.config

import java.util.concurrent.ConcurrentHashMap
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@Configuration
class LocalCacheConfig {
    @Bean
    fun clientCache(): ConcurrentHashMap<String, SseEmitter> {
        return ConcurrentHashMap()
    }

    @Bean
    fun connectionCache(): ConcurrentHashMap<String, MutableSet<String>> {
        return ConcurrentHashMap()
    }
}
