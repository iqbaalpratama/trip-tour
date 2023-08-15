package com.iqbaal.triptour.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iqbaal.triptour.dto.request.LoginUserRequest;
import com.iqbaal.triptour.dto.response.TokenResponse;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

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
    void testLoginFailed() throws Exception{
        LoginUserRequest loginUserRequest = new LoginUserRequest();
        loginUserRequest.setEmail("iqbaal@email.com");
        loginUserRequest.setPassword("rahasia");

        mockMvc.perform(
                post("/api/v1/auth/login")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(loginUserRequest))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo( result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testLoginFailedPassword() throws Exception{
        User user = new User();
        user.setEmail("employer@email.com");
        user.setFirstName("Iqbaal");
        user.setLastName("Putra");
        user.setGender('L');
        user.setAddress("Jalan Anggur 3 Jakarta");
        user.setPhone("081234567890");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setRole("employer");
        user.setCreatedDate(ZonedDateTime.now());
        userRepository.save(user);

        LoginUserRequest loginUserRequest = new LoginUserRequest();
        loginUserRequest.setEmail("employer@email.com");
        loginUserRequest.setPassword("salahpassword");

        mockMvc.perform(
                post("/api/v1/auth/login")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(loginUserRequest))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo( result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testLoginSuccess() throws Exception{
        User user = new User();
        user.setEmail("employer@email.com");
        user.setFirstName("Iqbaal");
        user.setLastName("Putra");
        user.setGender('L');
        user.setAddress("Jalan Anggur 3 Jakarta");
        user.setPhone("081234567890");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setRole("employer");
        user.setCreatedDate(ZonedDateTime.now());
        userRepository.save(user);

        LoginUserRequest loginUserRequest = new LoginUserRequest();
        loginUserRequest.setEmail("employer@email.com");
        loginUserRequest.setPassword("rahasia");

        mockMvc.perform(
                post("/api/v1/auth/login")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(loginUserRequest))
        ).andExpectAll(
                status().isOk()
        ).andDo( result -> {
            WebResponse<TokenResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
            assertNull(response.getErrors());
            assertNotNull(response.getData().getToken());
            assertNotNull(response.getData().getExpiresIn());

            Optional<User> userDb = userRepository.findByEmail(user.getEmail());
            if(userDb.isPresent()){
                assertNotNull(userDb);
                assertEquals(userDb.get().getToken(), response.getData().getToken());
                assertEquals(1800, response.getData().getExpiresIn());
            }
        });
    }
}
