package ru.ifmo.se.api.infrastructure.security.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import ru.ifmo.se.api.domain.model.PersonId
import ru.ifmo.se.api.domain.repository.PersonRepository

@Component
class JwtAuthenticationFilter(
    private val jwtAccessTokenGenerator: JwtAccessTokenGenerator,
    private val personRepository: PersonRepository,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val jwt = request.cookies?.firstOrNull { it.name == "accessToken" }?.value

        if (jwt != null) {
            val personId = jwtAccessTokenGenerator.validateAndGetPersonId(jwt)
            if (personId != null && SecurityContextHolder.getContext().authentication == null) {
                val person = personRepository.findById(PersonId(personId))
                if (person != null) {
                    val authorities = listOf(SimpleGrantedAuthority("ROLE_${person.role.name}"))
                    val authToken =
                        UsernamePasswordAuthenticationToken(
                            person,
                            null,
                            authorities,
                        )
                    authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authToken
                }
            }
        }
        filterChain.doFilter(request, response)
    }
}
