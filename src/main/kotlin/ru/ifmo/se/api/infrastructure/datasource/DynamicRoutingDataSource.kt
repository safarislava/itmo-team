package ru.ifmo.se.api.infrastructure.datasource

import org.springframework.beans.factory.ObjectProvider
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource

class DynamicRoutingDataSource(
    private val contextProvider: ObjectProvider<DataSourceContext>,
) : AbstractRoutingDataSource() {
    override fun determineCurrentLookupKey(): Any = contextProvider.ifAvailable?.getDataSourceType() ?: DataSourceType.MASTER
}
