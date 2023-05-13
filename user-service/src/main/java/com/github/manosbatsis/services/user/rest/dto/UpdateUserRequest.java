package com.github.manosbatsis.services.user.rest.dto;

import com.github.manosbatsis.services.user.validation.NullOrNotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    @Schema(example = "Firstname Lastname")
    @NullOrNotBlank
    private String fullName;

    @Schema(example = "Street 12, City, POSTCODE, Country")
    @NullOrNotBlank
    private String address;

    @Schema(example = "false")
    private Boolean active;
}
