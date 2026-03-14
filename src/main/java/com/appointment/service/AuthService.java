package com.appointment.service;

import com.appointment.dto.AuthDto;
import com.appointment.entity.User;
import com.appointment.repository.UserRepository;
import com.appointment.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service layer for authentication operations (register/login).
 */
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Register a new user (student or faculty).
     */
    public AuthDto.AuthResponse register(AuthDto.RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already registered: " + request.getEmail());
        }

        // Create and save user
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : User.Role.STUDENT);
        user.setDepartment(request.getDepartment());
        user.setPhone(request.getPhone());

        User savedUser = userRepository.save(user);

        // Generate JWT and return response
        String token = jwtUtils.generateToken(savedUser);
        return buildAuthResponse(token, savedUser);
    }

    /**
     * Authenticate existing user and return JWT token.
     */
    public AuthDto.AuthResponse login(AuthDto.LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = (User) auth.getPrincipal();
        String token = jwtUtils.generateToken(user);
        return buildAuthResponse(token, user);
    }

    private AuthDto.AuthResponse buildAuthResponse(String token, User user) {
        return new AuthDto.AuthResponse(
                token,
                "Bearer",
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                user.getDepartment()
        );
    }
}
