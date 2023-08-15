package com.iqbaal.triptour.service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.iqbaal.triptour.dto.request.RegisterUserRequest;
import com.iqbaal.triptour.dto.request.UpdateUserRequest;
import com.iqbaal.triptour.dto.response.UserResponse;
import com.iqbaal.triptour.entity.User;
import com.iqbaal.triptour.repository.UserRepository;
import com.iqbaal.triptour.security.BCrypt;
import com.iqbaal.triptour.service.utils.UserMapper;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public UserResponse registerUser(RegisterUserRequest registerUserRequest){
        validationService.validate(registerUserRequest);

        if(userRepository.existsByEmail(registerUserRequest.getEmail())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already registered");
        }
        User user = UserMapper.toUser(registerUserRequest);
        userRepository.save(user);
        return UserMapper.toUserResponse(user);
    }

    public UserResponse getUserById(String id){
        User user = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with that id not found"));
        return UserMapper.toUserResponse(user);
    }

    public List<UserResponse> getAllUser(){
        List<User> users = userRepository.findAll();
        return users.stream().map(UserMapper::toUserResponse).toList();
    }


    public UserResponse getCurrentUser(User user){
        return UserMapper.toUserResponse(user);
    }

    public UserResponse update(User user, UpdateUserRequest updateUserRequest){
        validationService.validate(updateUserRequest);

        if(Objects.nonNull(updateUserRequest.getEmail())){
            user.setEmail(updateUserRequest.getEmail());
        }
        if(Objects.nonNull(updateUserRequest.getPassword())){
            user.setPassword(BCrypt.hashpw(updateUserRequest.getPassword(), BCrypt.gensalt()));
        }
        if(Objects.nonNull(updateUserRequest.getFirstName())){
            user.setFirstName(updateUserRequest.getFirstName());
        }
        if(Objects.nonNull(updateUserRequest.getLastName())){
            user.setLastName(updateUserRequest.getLastName());
        }
        if(Objects.nonNull(updateUserRequest.getGender())){
            user.setGender(updateUserRequest.getGender());
        }
        if(Objects.nonNull(updateUserRequest.getAddress())){
            user.setAddress(updateUserRequest.getAddress());
        }
        if(Objects.nonNull(updateUserRequest.getPhone())){
            user.setPhone(updateUserRequest.getPhone());
        }

        userRepository.save(user);

        return UserMapper.toUserResponse(user);
    }

}
