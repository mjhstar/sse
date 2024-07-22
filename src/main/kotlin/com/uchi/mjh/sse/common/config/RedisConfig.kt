package com.uchi.mjh.sse.common.config

import com.uchi.mjh.sse.common.redis.RedisCache
import com.uchi.mjh.sse.common.support.JacksonExtension.mapper
import java.time.Duration
import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.cache.CacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.CacheKeyPrefix
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig(
    private val redisProperties: RedisProperties
) {
    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        return LettuceConnectionFactory(redisProperties.host, redisProperties.port)
    }

    @Bean
    fun redisTemplate(): RedisTemplate<String, Any> {
        return RedisTemplate<String, Any>().apply {
            this.connectionFactory = redisConnectionFactory()
            this.keySerializer = StringRedisSerializer()
            this.valueSerializer = GenericJackson2JsonRedisSerializer(mapper())
        }
    }

    @Bean
    fun redisCacheManager(): CacheManager {
        val redisSerializer = JdkSerializationRedisSerializer(javaClass.classLoader)

        val configuration = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofDays(ALIM_EXPIRE_DAY))
            .computePrefixWith(CacheKeyPrefix.simple())
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))

        val cacheConfiguration = mutableMapOf<String, RedisCacheConfiguration>()
        cacheConfiguration[RedisCache.CONNECTED_USER] = configuration.entryTtl(Duration.ofDays(ALIM_EXPIRE_DAY))

        return RedisCacheManager.RedisCacheManagerBuilder
            .fromConnectionFactory(redisConnectionFactory())
            .cacheDefaults(configuration)
            .withInitialCacheConfigurations(cacheConfiguration)
            .build()
    }

    @Bean
    fun redisMessageListenerContainer(): RedisMessageListenerContainer {
        return RedisMessageListenerContainer().apply {
            this.setConnectionFactory(redisConnectionFactory())
        }
    }

    companion object {
        const val ALIM_EXPIRE_DAY = 30L
    }
}
