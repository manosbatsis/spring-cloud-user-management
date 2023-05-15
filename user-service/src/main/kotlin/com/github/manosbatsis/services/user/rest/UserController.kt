package com.github.manosbatsis.services.user.rest

import com.github.manosbatsis.services.user.mapper.UserMapper
import com.github.manosbatsis.services.user.rest.dto.CreateUserRequest
import com.github.manosbatsis.services.user.rest.dto.UpdateUserRequest
import com.github.manosbatsis.services.user.rest.dto.UserResponse
import com.github.manosbatsis.services.user.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
) {

    private val userMapper = UserMapper.instance()

    @GetMapping
    fun users(): List<UserResponse> =
        userService.findAll().map { userMapper.toUserResponse(it) }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): UserResponse? {
        val user = userService.validateAndGetUserById(id)
        return userMapper.toUserResponse(user)
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun createUser(
        @RequestBody @Valid
        createUserRequest: CreateUserRequest,
    ): UserResponse {
        userService.emailAvailableOrThrow(createUserRequest.email!!)
        var user = userMapper.toUser(createUserRequest)
        user = userService.saveUser(user)
        return userMapper.toUserResponse(user)
    }

    @PutMapping("/{id}")
    fun updateUser(
        @PathVariable id: Long,
        @RequestBody @Valid
        updateUserRequest: UpdateUserRequest,
    ): UserResponse {
        var user = userService.validateAndGetUserById(id)
        userMapper.updateUserFromRequest(updateUserRequest, user)
        user = userService.saveUser(user)
        return userMapper.toUserResponse(user)
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): UserResponse {
        val user = userService.validateAndGetUserById(id)
        userService.deleteUser(user)
        return userMapper.toUserResponse(user)
    }
}
