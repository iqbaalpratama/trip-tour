package com.iqbaal.triptour.service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iqbaal.triptour.dto.request.LoginUserRequest;
import com.iqbaal.triptour.dto.request.RegisterUserRequest;
import com.iqbaal.triptour.dto.response.TokenResponse;
import com.iqbaal.triptour.entity.Role;
import com.iqbaal.triptour.entity.Token;
import com.iqbaal.triptour.entity.User;
import com.iqbaal.triptour.entity.utils.EnumRole;
import com.iqbaal.triptour.entity.utils.TokenType;
import com.iqbaal.triptour.repository.RoleRepository;
import com.iqbaal.triptour.repository.TokenRepository;
import com.iqbaal.triptour.repository.UserRepository;
import com.iqbaal.triptour.security.jwt.JwtUtils;
import com.iqbaal.triptour.service.utils.UserMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService implements LogoutHandler{

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final ValidationService validationService;


    @Transactional
    public TokenResponse register(RegisterUserRequest registerUserRequest) {
        validationService.validate(registerUserRequest);
        if(userRepository.existsByEmail(registerUserRequest.getEmail())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already registered");
        }
        this.processRegisterUserRequest(registerUserRequest);
        User user = UserMapper.toUser(registerUserRequest);
        User savedUser = userRepository.save(user);
        String jwtToken = jwtUtils.generateToken(user);
        String refreshToken = jwtUtils.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);
        return TokenResponse.builder()
            .token(jwtToken)
            .expiresIn(30*60*1000)
            .refreshToken(refreshToken)
            .build();
    }

    @Transactional
    public TokenResponse authenticate(LoginUserRequest loginUserRequest) {
        validationService.validate(loginUserRequest);
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginUserRequest.getEmail(),
                loginUserRequest.getPassword()
            )
        );
        var user = userRepository.findByEmail(loginUserRequest.getEmail())
            .orElseThrow();
        var jwtToken = jwtUtils.generateToken(user);
        var refreshToken = jwtUtils.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return TokenResponse.builder()
            .token(jwtToken)
            .expiresIn(30*60*1000)
            .refreshToken(refreshToken)
            .build();
    }

    @Transactional
    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
            .user(user)
            .token(jwtToken)
            .tokenType(TokenType.BEARER)
            .isExpired(false)
            .isRevoked(false)
            .build();
        tokenRepository.save(token);
    }

    @Transactional
    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty()){
            return;
        }
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    @Transactional
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtUtils.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.userRepository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtUtils.isTokenValid(refreshToken, user)) {
                var accessToken = jwtUtils.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = TokenResponse.builder()
                        .token(accessToken)
                        .expiresIn(30*60*100)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    @Override
    public void logout(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication
    ) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }
        jwt = authHeader.substring(7);
        var storedToken = tokenRepository.findByToken(jwt)
            .orElse(null);
        if (storedToken != null) {
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenRepository.save(storedToken);
            SecurityContextHolder.clearContext();
        }
    }

    private void processRegisterUserRequest(RegisterUserRequest registerUserRequest){
        registerUserRequest.setPassword(passwordEncoder.encode(registerUserRequest.getPassword()));
        Set<String> strRoles = registerUserRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(EnumRole.ROLE_CUSTOMER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "customer":
                        Role customerRole = roleRepository.findByName(EnumRole.ROLE_CUSTOMER)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(customerRole);
                    break;

                    case "employer":
                        Role employerRole = roleRepository.findByName(EnumRole.ROLE_EMPLOYER)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(employerRole);
                    break;

                    default:
                        Role defaultRole = roleRepository.findByName(EnumRole.ROLE_CUSTOMER)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(defaultRole);
                }
            });
        }
    }
}
