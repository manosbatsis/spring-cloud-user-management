package com.github.manosbatsis.services.user.service;

import com.github.manosbatsis.services.user.exception.UserEmailDuplicatedException;
import com.github.manosbatsis.services.user.exception.UserNotFoundException;
import com.github.manosbatsis.services.user.model.User;
import com.github.manosbatsis.services.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(User user) {
        // soft delete, see User annotations
        userRepository.delete(user);
    }

    @Override
    public User validateAndGetUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException());
    }

    @Override
    public void validateUserExistsByEmail(String email) {
        userRepository
                .findUserByEmail(email)
                .ifPresent(
                        user -> {
                            throw new UserEmailDuplicatedException(
                                    String.format("User with email '%s' already exist.", email));
                        });
    }
}
