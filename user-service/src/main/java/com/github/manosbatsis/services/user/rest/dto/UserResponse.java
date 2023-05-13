package com.github.manosbatsis.services.user.rest.dto;

public record UserResponse(Long id, String email, String fullName, String address, Boolean active) {
}
