package com.github.manosbatsis.services.user.service

import com.github.manosbatsis.services.user.exception.UserEmailDuplicatedException
import com.github.manosbatsis.services.user.exception.UserNotFoundException
import com.github.manosbatsis.services.user.model.User
import com.github.manosbatsis.services.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
) : UserService {
    override fun findAll(): List<User> = userRepository.findAll()

    override fun saveUser(user: User): User {
        return userRepository.save(user)
    }

    override fun deleteUser(user: User) {
        // soft delete, see User annotations
        userRepository.delete(user)
    }

    override fun validateAndGetUserById(id: Long): User {
        return userRepository.findById(id).orElseThrow { UserNotFoundException() }
    }

    override fun emailAvailableOrThrow(email: String) {
        userRepository.findUserByEmail(email)?.let {
            throw UserEmailDuplicatedException(
                String.format(
                    "User with email '%s' already exist.",
                    email,
                ),
            )
        }
    }
}
