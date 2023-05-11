package com.github.manosbatsis.userservice.mapper;

import com.github.manosbatsis.userservice.model.User;
import com.github.manosbatsis.userservice.rest.dto.CreateUserRequest;
import com.github.manosbatsis.userservice.rest.dto.UpdateUserRequest;
import com.github.manosbatsis.userservice.rest.dto.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.context.annotation.Configuration;

@Configuration
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    User toUser(CreateUserRequest createUserRequest);

    UserResponse toUserResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "email", ignore = true)
    void updateUserFromRequest(UpdateUserRequest updateUserRequest, @MappingTarget User user);
}
