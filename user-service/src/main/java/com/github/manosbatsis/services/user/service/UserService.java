package com.github.manosbatsis.services.user.service;

import com.github.manosbatsis.services.user.model.User;

import java.util.List;

public interface UserService {

    List<User> getUsers();

    User saveUser(User user);

    void deleteUser(User user);

    User validateAndGetUserById(Long id);

    void validateUserExistsByEmail(String email);
}
