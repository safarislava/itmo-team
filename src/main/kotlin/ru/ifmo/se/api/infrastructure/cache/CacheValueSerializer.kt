package ru.ifmo.se.api.infrastructure.cache

import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.springframework.core.KotlinDetector
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.util.ClassUtils
import tools.jackson.core.TreeNode
import tools.jackson.databind.DefaultTyping
import tools.jackson.databind.DeserializationFeature
import tools.jackson.databind.JavaType
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import tools.jackson.databind.jsontype.impl.DefaultTypeResolverBuilder
import tools.jackson.module.kotlin.KotlinModule

object CacheValueSerializer {
    fun json(): RedisSerializer<Any> {
        val typeValidator =
            BasicPolymorphicTypeValidator
                .builder()
                .allowIfBaseType(Any::class.java)
                .allowIfSubType { _, _ -> true }
                .build()

        val typeResolver =
            object : DefaultTypeResolverBuilder(
                typeValidator,
                DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY,
                JsonTypeInfo.Id.CLASS,
                "@class",
            ) {
                override fun useForType(javaType: JavaType): Boolean {
                    if (javaType.isJavaLangObject) return true
                    val raw = javaType.rawClass
                    if (raw.isAnnotationPresent(JvmInline::class.java)) return false
                    if (javaType.isEnumType || ClassUtils.isPrimitiveOrWrapper(raw)) return false
                    if (javaType.isFinal && !KotlinDetector.isKotlinType(raw) && raw.packageName.startsWith("java")) {
                        return false
                    }
                    return !TreeNode::class.java.isAssignableFrom(raw)
                }
            }

        return GenericJacksonJsonRedisSerializer.create { builder ->
            builder
                .enableSpringCacheNullValueSupport()
                .customize { mapperBuilder ->
                    mapperBuilder
                        .addModule(KotlinModule.Builder().build())
                        .configure(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY, false)
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                        .setDefaultTyping(typeResolver)
                }
        }
    }
}
