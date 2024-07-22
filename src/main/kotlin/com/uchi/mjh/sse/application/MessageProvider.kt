package com.uchi.mjh.sse.application

import com.uchi.mjh.sse.common.redis.RedisMessageProvider
import com.uchi.mjh.sse.data.MessageDto
import org.springframework.stereotype.Component

@Component
class MessageProvider(
    private val sseEmitterProvider: SseEmitterProvider,
    private val redisMessageProvider: RedisMessageProvider
) {
    fun publishMessage(message: MessageDto) {
        sendMessage(message)
    }

    private fun sendMessage(message: MessageDto) {
        val connectedUserSet = sseEmitterProvider.getConnectedUserSet()
        connectedUserSet.forEach {
            redisMessageProvider.publishMessage(it, message)
        }
    }
}
