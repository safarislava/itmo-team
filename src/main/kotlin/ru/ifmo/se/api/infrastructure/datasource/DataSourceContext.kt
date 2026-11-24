package ru.ifmo.se.api.infrastructure.datasource

import org.springframework.stereotype.Component

@Component
class DataSourceContext {
    private val stack = ThreadLocal.withInitial { ArrayDeque<DataSourceType>() }

    fun getDataSourceType(): DataSourceType = stack.get().firstOrNull() ?: DataSourceType.MASTER

    fun <T> withDataSourceType(
        type: DataSourceType,
        block: () -> T,
    ): T {
        val types = stack.get()
        types.addFirst(type)
        try {
            return block()
        } finally {
            types.removeFirst()
            if (types.isEmpty()) stack.remove()
        }
    }
}
