package ru.ifmo.se.api.infrastructure.datasource

import com.zaxxer.hikari.HikariDataSource
import liquibase.integration.spring.SpringLiquibase
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy
import javax.sql.DataSource

@Configuration
class DataSourceConfiguration {
    @Bean
    @ConfigurationProperties("spring.datasource.master")
    fun masterDataSource(): DataSource = DataSourceBuilder.create().type(HikariDataSource::class.java).build()

    @Bean
    @ConfigurationProperties("spring.datasource.slave")
    fun slaveDataSource(): DataSource = DataSourceBuilder.create().type(HikariDataSource::class.java).build()

    @Bean
    fun dynamicRoutingDataSource(
        @Qualifier("masterDataSource") master: DataSource,
        @Qualifier("slaveDataSource") slave: DataSource,
        contextProvider: ObjectProvider<DataSourceContext>,
    ): DynamicRoutingDataSource {
        val routing = DynamicRoutingDataSource(contextProvider)
        routing.setTargetDataSources(
            mapOf(
                DataSourceType.MASTER to master,
                DataSourceType.SLAVE to slave,
            ),
        )
        routing.setDefaultTargetDataSource(master)
        routing.afterPropertiesSet()
        return routing
    }

    @Bean
    @Primary
    fun dataSource(
        @Qualifier("dynamicRoutingDataSource") routing: DataSource,
    ): DataSource = LazyConnectionDataSourceProxy(routing)

    @Bean
    fun liquibase(
        @Qualifier("masterDataSource") masterDataSource: DataSource,
    ): SpringLiquibase =
        SpringLiquibase().apply {
            dataSource = masterDataSource
            changeLog = "classpath:db/changelog/db.changelog-master.xml"
        }
}
