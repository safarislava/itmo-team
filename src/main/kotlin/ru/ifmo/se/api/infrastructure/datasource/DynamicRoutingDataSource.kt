package ru.ifmo.se.api.infrastructure.datasource

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource

class DynamicRoutingDataSource(
    private val context: DataSourceContext,
) : AbstractRoutingDataSource() {
    override fun determineCurrentLookupKey(): Any = context.getDataSourceType()
}
