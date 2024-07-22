package com.uchi.mjh.sse.application

import com.uchi.mjh.sse.common.redis.RedisMessageProvider
import com.uchi.mjh.sse.data.MessageDto
import java.util.UUID
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@Service
class SseService(
    private val sseEmitterProvider: SseEmitterProvider,
    private val redisMessageProvider: RedisMessageProvider,
    private val sseMessageSender: SseMessageSender,
    private val messageProvider: MessageProvider
) {
    fun subscribe(userKey: String): SseEmitter {
        val clientId = sseEmitterProvider.makeClientId()
        val sseEmitter = sseEmitterProvider.createSseEmitter(userKey, clientId).apply {
            this.onTimeout(this::complete)
            this.onError {
                this.complete()
            }
            this.onCompletion {
                sseEmitterProvider.deleteEmitter(userKey, clientId)
                redisMessageProvider.removeMessageSubscribe(userKey)
            }
        }
        redisMessageProvider.subscribeMessage(userKey)
        sseMessageSender.connect(clientId, sseEmitter)
        return sseEmitter
    }

    fun sendMessage(userKey: String, message: String) {
        val message = MessageDto(
            messageId = UUID.randomUUID().toString(),
            message = message
        )
        redisMessageProvider.publishMessage(userKey, message).also {
            messageProvider.publishMessage(message)
        }
    }
}
