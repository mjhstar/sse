package com.uchi.mjh.sse.repository

import com.uchi.mjh.sse.data.SseEmitterDto
import java.util.concurrent.ConcurrentHashMap
import org.springframework.stereotype.Repository
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@Repository
class SseEmitterRepository(
    private val clientCache: ConcurrentHashMap<String, SseEmitter>,
    private val connectionCache: ConcurrentHashMap<String, MutableSet<String>>
) {
    fun save(userKey: String, clientId: String): SseEmitter {
        val sseEmitter = SseEmitter(TIME_OUT)
        val connections = connectionCache[userKey] ?: mutableSetOf()
        connections.add(clientId)
        connectionCache[userKey] = connections
        clientCache[clientId] = sseEmitter
        return sseEmitter
    }

    fun delete(userKey: String, clientId: String) {
        val connections = connectionCache[userKey] ?: mutableSetOf()
        clientCache.remove(clientId)
        if (connections.isEmpty()) {
            connectionCache.remove(userKey)
        } else {
            connections.remove(clientId)
        }
        connectionCache[userKey] = connections
    }

    fun isEmptyClient(userKey: String): Boolean {
        return connectionCache[userKey]?.isEmpty() ?: true
    }

    fun findByUserKey(userKey: String): List<SseEmitterDto> {
        val clientIds = connectionCache[userKey] ?: mutableSetOf()
        return clientIds.mapNotNull { clientId ->
            clientCache[clientId]?.let { SseEmitterDto(clientId, it) }
        }
    }

    companion object {
        private const val TIME_OUT = 1000 * 60 * 30L;
    }
}
