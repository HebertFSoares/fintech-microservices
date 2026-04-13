package com.authservice.mapper;

import com.authservice.dto.RegisterResponse;
import com.authservice.dto.UserResponse;
import com.authservice.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    RegisterResponse toRegisterResponse(User user);

    UserResponse toUserResponse(User user);

}