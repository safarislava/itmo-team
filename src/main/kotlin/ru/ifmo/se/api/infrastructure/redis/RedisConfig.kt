package ru.ifmo.se.api.infrastructure.redis

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import ru.ifmo.se.api.infrastructure.redis.entity.RefreshTokenEntity
import tools.jackson.databind.ObjectMapper

@Configuration
class RedisConfig {
    @Bean
    fun refreshTokenRedisTemplate(
        connectionFactory: RedisConnectionFactory,
        objectMapper: ObjectMapper,
    ): RedisTemplate<String, RefreshTokenEntity> =
        RedisTemplate<String, RefreshTokenEntity>().apply {
            setConnectionFactory(connectionFactory)
            keySerializer = StringRedisSerializer()
            valueSerializer = JacksonJsonRedisSerializer(objectMapper, RefreshTokenEntity::class.java)
            afterPropertiesSet()
        }
}
