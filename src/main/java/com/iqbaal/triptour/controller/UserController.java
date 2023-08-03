package com.iqbaal.triptour.controller;

import com.iqbaal.triptour.entity.User;
import com.iqbaal.triptour.model.request.RegisterUserRequest;
import com.iqbaal.triptour.model.request.UpdateUserRequest;
import com.iqbaal.triptour.model.response.UserResponse;
import com.iqbaal.triptour.model.response.WebResponse;
import com.iqbaal.triptour.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(path = "/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(
            path = "/get-all",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<UserResponse>> getAllUsers(){
        List<UserResponse> userResponses = userService.getAllUser();
        return WebResponse.<List<UserResponse>>builder().data(userResponses).build();
    }

    @GetMapping(
            path = "/get",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> getUserById(@RequestParam String id){
        UserResponse userResponse = userService.getUserById(id);
        return WebResponse.<UserResponse>builder().data(userResponse).build();
    }

    @PostMapping(
            path = "/register-employer",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> register(@RequestBody RegisterUserRequest registerUserRequest){
        userService.registerUser(registerUserRequest, "employer");
        return WebResponse.<String>builder().data("OK").build();
    }

    @GetMapping(
            path = "/current",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> getCurrent(User user){
        UserResponse userResponse = userService.getCurrentUser(user);
        return WebResponse.<UserResponse>builder().data(userResponse).build();
    }

    @PatchMapping(
            path = "/update",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> update(User user, @RequestBody UpdateUserRequest updateUserRequest){
        UserResponse userResponse = userService.update(user, updateUserRequest);
        return WebResponse.<UserResponse>builder().data(userResponse).build();
    }

}
