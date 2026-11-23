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
    @Around("@annotation(ru.ifmo.se.api.application.annotation.ReadOnlyDataAссess) || @within(ru.ifmo.se.api.application.annotation.ReadOnlyDataAссess)")
private fun setSlaveDataSource(joinPoint: ProceedingJoinPoint): Any? {
        return context.withDataSourceType(DataSourceType.SLAVE) {
            joinPoint.proceed()
        }
    }

    @Around("@annotation(ru.ifmo.se.api.application.annotation.WriteDataAссess) || @within(ru.ifmo.se.api.application.annotation.WriteDataAссess)")
private fun setMasterDataSource(joinPoint: ProceedingJoinPoint): Any? {
        return context.withDataSourceType(DataSourceType.MASTER) {
            joinPoint.proceed()
        }
    }
}