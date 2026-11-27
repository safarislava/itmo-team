package ru.ifmo.se.api.infrastructure.cache

import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

@Configuration
@EnableCaching
class CacheConfig(
    private val resolvers: Map<String, CacheKeyResolver>,
) {
    @Bean
    fun cacheManager(connectionFactory: RedisConnectionFactory): RedisCacheManager {
        val defaultConfig =
            RedisCacheConfiguration
                .defaultCacheConfig()
                .disableKeyPrefix()
                .entryTtl(Duration.ofHours(24))
                .serializeKeysWith(fromSerializer(StringRedisSerializer()))
                .serializeValuesWith(fromSerializer(CacheValueSerializer.json()))

        val cacheConfigurations =
            resolvers.mapValues { (_, resolver) ->
                defaultConfig
                    .entryTtl(resolver.getTtl())
                    .serializeKeysWith(fromSerializer(resolvedKeySerializer(resolver)))
            }

        return RedisCacheManager
            .builder(connectionFactory)
            .cacheDefaults(defaultConfig)
            .withInitialCacheConfigurations(cacheConfigurations)
            .build()
    }

    private fun resolvedKeySerializer(resolver: CacheKeyResolver): RedisSerializer<String> =
        object : RedisSerializer<String> {
            override fun serialize(t: String?): ByteArray = t?.let { resolver.resolveKey(it).toByteArray(Charsets.UTF_8) } ?: ByteArray(0)

            override fun deserialize(bytes: ByteArray?): String? = bytes?.let { String(it, Charsets.UTF_8) }
        }
}
