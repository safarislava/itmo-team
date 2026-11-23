package ru.ifmo.se.api.infrastructure.datasource

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Aspect
@Component
@Order(0)
class DataSourceAspect(
    private val context: DataSourceContext,
) {
    @Around(
        "@annotation(ru.ifmo.se.api.application.annotation.ReadOnlyDataAccess) || @within(ru.ifmo.se.api.application.annotation.ReadOnlyDataAccess)",
    )
    fun setSlaveDataSource(joinPoint: ProceedingJoinPoint): Any? =
        context.withDataSourceType(DataSourceType.SLAVE) {
            joinPoint.proceed()
        }

    @Around(
        "@annotation(ru.ifmo.se.api.application.annotation.WriteDataAccess) || @within(ru.ifmo.se.api.application.annotation.WriteDataAccess)",
    )
    fun setMasterDataSource(joinPoint: ProceedingJoinPoint): Any? =
        context.withDataSourceType(DataSourceType.MASTER) {
            joinPoint.proceed()
        }
}
