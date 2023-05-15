package com.github.manosbatsis.services.user.service

import com.github.manosbatsis.services.user.model.User

interface UserService {

    fun findAll(): List<User>
    fun saveUser(user: User): User
    fun deleteUser(user: User)
    fun validateAndGetUserById(id: Long): User
    fun emailAvailableOrThrow(email: String)
}
