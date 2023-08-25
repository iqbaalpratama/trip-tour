package com.iqbaal.triptour.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iqbaal.triptour.dto.request.RegisterUserRequest;
import com.iqbaal.triptour.dto.response.WebResponse;
import com.iqbaal.triptour.entity.User;
import com.iqbaal.triptour.repository.UserRepository;
import com.iqbaal.triptour.security.BCrypt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        userRepository.deleteAll();
    }

    @Test
    void testRegisterSuccess() throws Exception {
        RegisterUserRequest registerUserRequest = new RegisterUserRequest();
        registerUserRequest.setEmail("employer@email.com");
        registerUserRequest.setFirstName("Iqbaal");
        registerUserRequest.setLastName("Putra");
        registerUserRequest.setGender('L');
        registerUserRequest.setAddress("Jalan Anggur 3 Jakarta");
        registerUserRequest.setPhone("081234567890");
        registerUserRequest.setPassword("rahasia");

        mockMvc.perform(
                post("/api/v1/users/register-employer")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(registerUserRequest))
        ).andExpectAll(
                status().isOk()
        ).andDo( result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){
            });
            assertEquals("OK", response.getData());
        });
    }

    @Test
    void testRegisterBadRequest() throws Exception {
        RegisterUserRequest registerUserRequest = new RegisterUserRequest();
        registerUserRequest.setEmail("");
        registerUserRequest.setFirstName("");
        registerUserRequest.setLastName("Putra");
        registerUserRequest.setGender('L');
        registerUserRequest.setAddress("Jalan Anggur 3 Jakarta");
        registerUserRequest.setPhone("081234567890");
        registerUserRequest.setPassword("rahasia");

        mockMvc.perform(
                post("/api/v1/users/register-employer")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(registerUserRequest))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo( result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testRegisterDuplicate() throws Exception {
        User user = new User();
        user.setEmail("employer@email.com");
        user.setFirstName("Iqbaal");
        user.setLastName("Putra");
        user.setGender('L');
        user.setAddress("Jalan Anggur 3 Jakarta");
        user.setPhone("081234567890");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setCreatedDate(ZonedDateTime.now());
        userRepository.save(user);

        RegisterUserRequest registerUserRequest = new RegisterUserRequest();
        registerUserRequest.setEmail("employer@email.com");
        registerUserRequest.setFirstName("Iqbaal");
        registerUserRequest.setLastName("Putra");
        registerUserRequest.setGender('L');
        registerUserRequest.setAddress("Jalan Anggur 3 Jakarta");
        registerUserRequest.setPhone("081234567890");
        registerUserRequest.setPassword("rahasia");

        mockMvc.perform(
                post("/api/v1/users/register-employer")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(registerUserRequest))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo( result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){
            });
            assertNotNull(response.getErrors());
        });
    }
}
