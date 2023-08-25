package com.iqbaal.triptour.service.utils;

import java.time.ZonedDateTime;

import com.iqbaal.triptour.dto.request.RegisterUserRequest;
import com.iqbaal.triptour.dto.response.UserResponse;
import com.iqbaal.triptour.entity.User;

public class UserMapper {

    public static User toUser(RegisterUserRequest registerUserRequest){
        User user = new User();
        user.setEmail(registerUserRequest.getEmail());
        user.setFirstName(registerUserRequest.getFirstName());
        user.setLastName(registerUserRequest.getLastName());
        user.setAddress(registerUserRequest.getAddress());
        user.setPhone(registerUserRequest.getPhone());
        user.setGender(registerUserRequest.getGender());
        user.setPassword(registerUserRequest.getPassword());
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
                .build();
    }
}
