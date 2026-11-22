package ru.ifmo.se.api.presentation.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.stereotype.Component
import ru.ifmo.se.api.application.port.out.CityValidator

@Component
class CityConstraintValidator(
    private val cityValidator: CityValidator,
) : ConstraintValidator<ValidCity, String?> {
    override fun isValid(
        value: String?,
        context: ConstraintValidatorContext?,
    ): Boolean {
        if (value.isNullOrBlank()) {
            return true
        }
        return cityValidator.isValid(value)
    }
}
