package com.uchi.mjh.sse.common.redis

import com.uchi.mjh.sse.common.support.convert
import org.springframework.stereotype.Component

@Component
class RedisProvider(
    private val redisCache: RedisCache
) {

    fun putConnectedUser(userKey: String) {
        val connectedUsers = getConnectedUser()
        connectedUsers.add(userKey)
        redisCache.putValue(RedisCache.CONNECTED_USER, RedisCache.CONNECTED_USER, connectedUsers)
    }

    fun getConnectedUser(): MutableSet<String> {
        return redisCache.getValue(RedisCache.CONNECTED_USER, RedisCache.CONNECTED_USER)?.let {
            it.get() as MutableSet<String>
        } ?: mutableSetOf()
    }

    fun removeConnectedUser(userKey: String) {
        val connectedUserSet = getConnectedUser()
        connectedUserSet.remove(userKey)
        redisCache.putValue(RedisCache.CONNECTED_USER, RedisCache.convert(), connectedUserSet)
    }
}
