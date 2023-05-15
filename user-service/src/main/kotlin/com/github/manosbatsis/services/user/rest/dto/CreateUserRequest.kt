package com.github.manosbatsis.services.user.rest.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CreateUserRequest(
    @field:Schema(example = "name@example.com")
    @field:NotBlank
    @field:Email
    val email: String? = null,

    @field:Schema(example = "Firstname Lastname")
    @field:NotBlank
    val fullName: String? = null,

    @field:Schema(example = "Street 12, City, POSTCODE, Country")
    @field:NotBlank
    val address: String? = null,

    @field:Schema(example = "true")
    @field:NotNull
    val active: Boolean? = null,
) {
    data class Validated(
        val email: String,
        val fullName: String,
        val address: String,
        val active: Boolean,
    )

    fun asValidated(): Validated {
        val valid = listOf(email, fullName, address, active).none {
            it == null || it.toString().isEmpty()
        }
        assert(valid) { "Invalid request: $this" }

        return Validated(email!!, fullName!!, address!!, active!!)
    }
}
