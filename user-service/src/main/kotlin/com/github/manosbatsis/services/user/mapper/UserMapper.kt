package com.github.manosbatsis.services.user.mapper

import com.github.manosbatsis.services.user.model.User
import com.github.manosbatsis.services.user.rest.dto.CreateUserRequest
import com.github.manosbatsis.services.user.rest.dto.UpdateUserRequest
import com.github.manosbatsis.services.user.rest.dto.UserResponse
import org.mapstruct.MappingTarget

object UserMapper {

    fun instance() = this

    fun toUser(createUserRequest: CreateUserRequest): User {
        val validRequest = createUserRequest.asValidated()
        return User(
            email = validRequest.email,
            fullName = validRequest.fullName,
            address = validRequest.address,
            active = validRequest.active,
        )
    }
    fun toUserResponse(user: User): UserResponse {
        return UserResponse(
            id = user.id,
            email = user.email,
            fullName = user.fullName,
            address = user.address,
            active = user.active,
        )
    }

    fun updateUserFromRequest(updateUserRequest: UpdateUserRequest, @MappingTarget user: User) {
        updateUserRequest.address?.let { user.address = it }
        updateUserRequest.fullName?.let { user.fullName = it }
        updateUserRequest.active?.let { user.active = it }
    }
}
