package com.iqbaal.triptour.service.utils;

import java.time.ZonedDateTime;
import java.util.Objects;

import com.iqbaal.triptour.dto.request.RegisterUserRequest;
import com.iqbaal.triptour.dto.response.UserResponse;
import com.iqbaal.triptour.entity.User;
import com.iqbaal.triptour.security.BCrypt;

public class UserMapper {

    public static User toUser(RegisterUserRequest registerUserRequest){
        User user = new User();
        user.setEmail(registerUserRequest.getEmail());
        user.setFirstName(registerUserRequest.getFirstName());
        user.setLastName(registerUserRequest.getLastName());
        user.setAddress(registerUserRequest.getAddress());
        user.setPhone(registerUserRequest.getPhone());
        user.setGender(registerUserRequest.getGender());
        user.setPassword(BCrypt.hashpw(registerUserRequest.getPassword(), BCrypt.gensalt()));
        user.setRole( Objects.nonNull(registerUserRequest.getRole()) ? registerUserRequest.getRole() : "employer");
        user.setCreatedDate(ZonedDateTime.now());
        return user;
    }

    public static UserResponse toUserResponse(User user){
        return UserResponse.builder()
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .gender(user.getGender() == 'L' ? "Laki-laki" : "Perempuan")
                .address(user.getAddress())
                .phone(user.getPhone())
                .role(user.getRole())
                .build();
    }
}
