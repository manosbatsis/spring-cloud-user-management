package com.github.manosbatsis.services.user.validation.validator

import com.github.manosbatsis.services.user.validation.NullOrNotBlank
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class NullOrNotBlankValidator : ConstraintValidator<NullOrNotBlank?, String?> {
    override fun initialize(parameters: NullOrNotBlank?) {
        // Nothing to do here
    }

    override fun isValid(value: String?, constraintValidatorContext: ConstraintValidatorContext): Boolean {
        return value == null || value.trim { it <= ' ' }.isNotEmpty()
    }
}
