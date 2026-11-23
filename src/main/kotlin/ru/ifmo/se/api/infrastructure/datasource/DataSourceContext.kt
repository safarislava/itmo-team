package ru.ifmo.se.api.infrastructure.datasource

import org.springframework.stereotype.Component
import org.springframework.web.context.annotation.RequestScope

@Component
@RequestScope
class DataSourceContext {
    private val stack = ArrayDeque<DataSourceType>()

    fun getDataSourceType(): DataSourceType = stack.firstOrNull() ?: DataSourceType.MASTER

    fun <T> withDataSourceType(
        type: DataSourceType,
        block: () -> T,
    ): T {
        stack.addFirst(type)
        try {
            return block()
        } finally {
            stack.removeFirstOrNull()
        }
    }
}
