package com.uchi.mjh.sse.application

import com.uchi.mjh.sse.common.support.toObject
import com.uchi.mjh.sse.data.MessageDto
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.stereotype.Component

@Component
class MessageListener(
    private val sseMessageSender: SseMessageSender
) : MessageListener {

    override fun onMessage(message: Message, pattern: ByteArray?) {
        try {
            val userKey = String(message.channel)
            val message = message.body.toObject(MessageDto::class.java) ?: return
            sseMessageSender.send(userKey, message)
        } catch (e: Exception) {

        }
    }
}
