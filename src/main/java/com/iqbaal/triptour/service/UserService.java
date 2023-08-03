package com.iqbaal.triptour.service;

import com.iqbaal.triptour.entity.User;
import com.iqbaal.triptour.model.request.RegisterUserRequest;
import com.iqbaal.triptour.model.request.UpdateUserRequest;
import com.iqbaal.triptour.model.response.UserResponse;
import com.iqbaal.triptour.repository.UserRepository;
import com.iqbaal.triptour.security.BCrypt;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public void registerUser(RegisterUserRequest registerUserRequest, String role){
        validationService.validate(registerUserRequest);

        if(userRepository.existsByEmail(registerUserRequest.getEmail())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already registered");
        }
        User user = new User();
        user.setEmail(registerUserRequest.getEmail());
        user.setFirstName(registerUserRequest.getFirstName());
        user.setLastName(registerUserRequest.getLastName());
        user.setAddress(registerUserRequest.getAddress());
        user.setPhone(registerUserRequest.getPhone());
        user.setGender(registerUserRequest.getGender());
        user.setPassword(BCrypt.hashpw(registerUserRequest.getPassword(), BCrypt.gensalt()));
        user.setRole(role);
        user.setCreatedDate(ZonedDateTime.now());
        userRepository.save(user);
    }

    public UserResponse getUserById(String id){
        User user = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with that id not found"));
        return toUserResponse(user);
    }

    public List<UserResponse> getAllUser(){
        List<User> users = userRepository.findAll();
        return users.stream().map(this::toUserResponse).toList();
    }


    public UserResponse getCurrentUser(User user){
        return toUserResponse(user);
    }

    private UserResponse toUserResponse(User user){
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

        return UserResponse.builder()
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .gender(user.getGender() == 'L' ? "Laki-laki" : "Perempuan")
                .phone(user.getPhone())
                .address(user.getAddress())
                .role(user.getRole())
                .build();
    }

}
