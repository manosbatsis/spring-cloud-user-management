package com.github.manosbatsis.userservice.service;

import com.github.manosbatsis.userservice.model.User;

import java.util.List;

public interface UserService {

    List<User> getUsers();

    User saveUser(User user);

    void deleteUser(User user);

    User validateAndGetUserById(Long id);

    void validateUserExistsByEmail(String email);
}
