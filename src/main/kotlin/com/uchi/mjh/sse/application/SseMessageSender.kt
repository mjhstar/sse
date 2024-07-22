package com.uchi.mjh.sse.application

import com.uchi.mjh.sse.data.MessageDto
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@Component
class SseMessageSender(
    private val sseEmitterProvider: SseEmitterProvider
) {
    fun connect(clientId: String, sseEmitter: SseEmitter) {
        try {
            sseEmitter.send(
                SseEmitter.event()
                    .id(clientId)
                    .name("connect")
                    .data("connected... $clientId")
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun send(userKey: String, message: MessageDto) {
        sseEmitterProvider.findSseEmitters(userKey).forEach {
            send(it.clientId, message, it.sseEmitter)
        }
    }

    private fun send(clientId: String, message: MessageDto, sseEmitter: SseEmitter) {
        try {
            sseEmitter.send(
                SseEmitter.event()
                    .id(clientId)
                    .name("message")
                    .data(message)
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
