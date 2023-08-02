package com.iqbaal.triptour.service;

import com.iqbaal.triptour.entity.User;
import com.iqbaal.triptour.model.request.LoginUserRequest;
import com.iqbaal.triptour.model.response.TokenResponse;
import com.iqbaal.triptour.repository.UserRepository;
import com.iqbaal.triptour.security.BCrypt;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public TokenResponse login(LoginUserRequest loginUserRequest){
        validationService.validate(loginUserRequest);

        User user = userRepository.findByEmail(loginUserRequest.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email or Password wrong"));

        if(BCrypt.checkpw(loginUserRequest.getPassword(), user.getPassword())){
            user.setToken(UUID.randomUUID().toString());
            user.setTokenExpiredAt(next30Minutes());
            userRepository.save(user);
            return TokenResponse.builder()
                    .token(user.getToken())
                    .expiresIn(1800)
                    .build();
        }else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email or Password wrong");
        }
    }

    @Transactional
    public void logout(User user){
        user.setToken(null);
        user.setTokenExpiredAt(null);
        userRepository.save(user);
    }

    private Long next30Minutes(){
        return System.currentTimeMillis() + (30 * 60 * 1000);
    }
}
