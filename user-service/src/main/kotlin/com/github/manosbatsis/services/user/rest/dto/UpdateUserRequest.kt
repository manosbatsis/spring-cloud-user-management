package com.github.manosbatsis.services.user.rest.dto

import com.github.manosbatsis.services.user.validation.NullOrNotBlank
import io.swagger.v3.oas.annotations.media.Schema

data class UpdateUserRequest(
    @field:Schema(example = "Firstname Lastname")
    @field:NullOrNotBlank
    var fullName: String? = null,

    @field:Schema(example = "Street 12, City, POSTCODE, Country")
    @field:NullOrNotBlank
    var address: String? = null,

    @field:Schema(example = "false")
    var active: Boolean? = null,
)
