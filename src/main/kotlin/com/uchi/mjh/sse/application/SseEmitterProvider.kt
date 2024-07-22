package com.uchi.mjh.sse.application

import com.uchi.mjh.sse.common.redis.RedisProvider
import com.uchi.mjh.sse.data.SseEmitterDto
import com.uchi.mjh.sse.repository.SseEmitterRepository
import java.util.UUID
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@Component
class SseEmitterProvider(
    private val sseEmitterRepository: SseEmitterRepository,
    private val redisProvider: RedisProvider
) {
    fun makeClientId(): String {
        return UUID.randomUUID().toString()
    }

    fun createSseEmitter(userKey: String, clientId: String): SseEmitter {
        redisProvider.putConnectedUser(userKey)
        return sseEmitterRepository.save(userKey, clientId)
    }

    fun getConnectedUserSet(): MutableSet<String> {
        return redisProvider.getConnectedUser()
    }

    fun deleteEmitter(userKey: String, clientId: String) {
        sseEmitterRepository.delete(userKey, clientId)
        if (sseEmitterRepository.isEmptyClient(userKey)) {
            redisProvider.removeConnectedUser(userKey)
        }
    }

    fun findSseEmitters(userKey: String): List<SseEmitterDto> {
        return sseEmitterRepository.findByUserKey(userKey)
    }
}
