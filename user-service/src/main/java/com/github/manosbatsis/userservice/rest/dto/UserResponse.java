package com.github.manosbatsis.userservice.rest.dto;

public record UserResponse(Long id, String email, String fullName, String address, Boolean active) {
}
