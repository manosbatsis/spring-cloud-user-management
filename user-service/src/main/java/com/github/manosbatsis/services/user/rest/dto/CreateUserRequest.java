package com.github.manosbatsis.services.user.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

    @Schema(example = "name@example.com")
    @NotBlank
    @Email
    private String email;

    @Schema(example = "Firstname Lastname")
    @NotBlank
    private String fullName;

    @Schema(example = "Street 12, City, POSTCODE, Country")
    @NotBlank
    private String address;

    @Schema(example = "true")
    @NotNull
    private Boolean active;
}
