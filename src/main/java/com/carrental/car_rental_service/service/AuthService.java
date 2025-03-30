package com.carrental.car_rental_service.service;

import com.carrental.car_rental_service.dto.AuthResponse;
import com.carrental.car_rental_service.dto.LoginRequest;
import com.carrental.car_rental_service.dto.RegisterRequest;
import com.carrental.car_rental_service.entity.Role;
import com.carrental.car_rental_service.entity.User;
import com.carrental.car_rental_service.repository.RoleRepository;
import com.carrental.car_rental_service.repository.UserRepository;
import com.carrental.car_rental_service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;

    public AuthResponse regiser(RegisterRequest request){
        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new RuntimeException("User already exists with email: " + request.getEmail());
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(()-> new RuntimeException("Role USER not found"));

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Collections.singleton(userRole))
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail());

        return new AuthResponse(token);

    }

    public AuthResponse login(LoginRequest request){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        String token = jwtUtil.generateToken(request.getEmail());
        return new AuthResponse(token);
    }
}
