package com.uchi.mjh.sse.common.redis

import org.springframework.cache.Cache.ValueWrapper
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Component

@Component
class RedisCache(
    private val cacheManager: CacheManager
) {
    fun getValue(cacheKey: String, dataKey: Any): ValueWrapper? {
        return try {
            cacheManager.getCache(cacheKey)?.get(dataKey)
        } catch (e: Exception) {
            cacheManager.getCache(cacheKey)?.evictIfPresent(dataKey)
            null
        }
    }

    fun <T> getValue(cacheKey: String, dataKey: Any, type: Class<T>): T? {
        return try {
            cacheManager.getCache(cacheKey)?.get(dataKey, type)
        } catch (e: Exception) {
            cacheManager.getCache(cacheKey)?.evictIfPresent(dataKey)
            null
        }
    }

    fun putValue(cacheKey: String, dataKey: Any, value: Any) {
        try {
            cacheManager.getCache(cacheKey)?.put(dataKey, value)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun removeValue(cacheKey: String, dataKey: Any) {
        try {
            cacheManager.getCache(cacheKey)?.evictIfPresent(dataKey)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        const val CONNECTED_USER = "connectedUser"
    }
}
