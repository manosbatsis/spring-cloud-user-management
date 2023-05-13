package com.github.manosbatsis.services.user.rest;

import com.github.manosbatsis.services.user.mapper.UserMapper;
import com.github.manosbatsis.services.user.model.User;
import com.github.manosbatsis.services.user.rest.dto.CreateUserRequest;
import com.github.manosbatsis.services.user.rest.dto.UpdateUserRequest;
import com.github.manosbatsis.services.user.rest.dto.UserResponse;
import com.github.manosbatsis.services.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    public List<UserResponse> getUsers() {
        return userService.getUsers()
                .stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        User user = userService.validateAndGetUserById(id);
        return userMapper.toUserResponse(user);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        userService.validateUserExistsByEmail(createUserRequest.getEmail());
        User user = userMapper.toUser(createUserRequest);
        user = userService.saveUser(user);
        return userMapper.toUserResponse(user);
    }

    @PutMapping("/{id}")
    public UserResponse updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        User user = userService.validateAndGetUserById(id);
        userMapper.updateUserFromRequest(updateUserRequest, user);
        user = userService.saveUser(user);
        return userMapper.toUserResponse(user);
    }

    @DeleteMapping("/{id}")
    public UserResponse deleteUser(@PathVariable Long id) {
        User user = userService.validateAndGetUserById(id);
        userService.deleteUser(user);
        return userMapper.toUserResponse(user);
    }
}
