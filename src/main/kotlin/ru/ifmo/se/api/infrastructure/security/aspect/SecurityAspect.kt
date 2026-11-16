package ru.ifmo.se.api.infrastructure.security.aspect

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.authentication.InsufficientAuthenticationException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import ru.ifmo.se.api.domain.model.Person
import ru.ifmo.se.api.domain.model.PersonRole
import ru.ifmo.se.api.infrastructure.security.annotation.Authenticated
import ru.ifmo.se.api.infrastructure.security.annotation.RequiresRole

@Aspect
@Component
class SecurityAspect {
    @Pointcut(
        "@annotation(ru.ifmo.se.api.infrastructure.security.annotation.Authenticated) || " +
            "@within(ru.ifmo.se.api.infrastructure.security.annotation.Authenticated) || " +
            "@annotation(ru.ifmo.se.api.infrastructure.security.annotation.RequiresRole) || " +
            "@within(ru.ifmo.se.api.infrastructure.security.annotation.RequiresRole)",
    )
    fun securityScope() {}

    @Before("securityScope()")
    fun checkSecurity(joinPoint: JoinPoint) {
        val signature = joinPoint.signature as? MethodSignature ?: return
        val method = signature.method
        val targetClass = joinPoint.target.javaClass

        val methodRole = AnnotatedElementUtils.findMergedAnnotation(method, RequiresRole::class.java)
        val classRole = AnnotatedElementUtils.findMergedAnnotation(targetClass, RequiresRole::class.java)

        if (methodRole != null || classRole != null) {
            val roles =
                buildSet {
                    if (methodRole != null) addAll(methodRole.value)
                    if (classRole != null) addAll(classRole.value)
                }
            verifyRole(roles)
            return
        }

        val methodAuth = AnnotatedElementUtils.findMergedAnnotation(method, Authenticated::class.java)
        val classAuth = AnnotatedElementUtils.findMergedAnnotation(targetClass, Authenticated::class.java)

        if (methodAuth != null || classAuth != null) {
            verifyAuthenticated()
            return
        }
    }

    private fun verifyAuthenticated(): Authentication {
        val auth = SecurityContextHolder.getContext().authentication
        if (auth == null || !auth.isAuthenticated || auth is AnonymousAuthenticationToken) {
            throw InsufficientAuthenticationException("Authentication is required to access this resource.")
        }
        return auth
    }

    private fun verifyRole(allowedRoles: Set<PersonRole>) {
        val auth = verifyAuthenticated()
        if (allowedRoles.isEmpty()) {
            return
        }

        val userRole = (auth.principal as? Person)?.role
        val hasRole =
            if (userRole != null) {
                userRole in allowedRoles
            } else {
                val allowedNames = allowedRoles.map { "ROLE_${it.name}" }.toSet()
                auth.authorities.any { it.authority in allowedNames }
            }

        if (!hasRole) {
            throw AccessDeniedException("Access is denied. User role does not have required permissions.")
        }
    }
}
