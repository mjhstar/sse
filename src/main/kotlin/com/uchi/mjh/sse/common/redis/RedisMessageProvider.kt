package com.uchi.mjh.sse.common.redis

import com.uchi.mjh.sse.application.MessageListener
import com.uchi.mjh.sse.data.MessageDto
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.stereotype.Component

@Component
class RedisMessageProvider(
    private val container: RedisMessageListenerContainer,
    private val redisTemplate: RedisTemplate<String, Any>,
    private val messageListener: MessageListener
) {
    fun subscribeMessage(userKey: String) {
        container.addMessageListener(messageListener, ChannelTopic.of(userKey))
    }

    fun publishMessage(userKey: String, message: MessageDto) {
        redisTemplate.convertAndSend(userKey, message)
    }

    fun removeMessageSubscribe(userKey: String) {
        container.removeMessageListener(messageListener, ChannelTopic.of(userKey))
    }
}
