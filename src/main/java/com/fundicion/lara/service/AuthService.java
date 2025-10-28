package com.fundicion.lara.service;

import com.fundicion.lara.commons.emuns.Role;
import com.fundicion.lara.config.security.RateLimiter;
import com.fundicion.lara.dto.UserDto;
import com.fundicion.lara.dto.request.LoginRequest;
import com.fundicion.lara.dto.request.RefreshTokenRequest;
import com.fundicion.lara.dto.request.UserRequest;
import com.fundicion.lara.dto.response.AuthResponse;
import com.fundicion.lara.entity.UserEntity;
import com.fundicion.lara.exception.NotFoundException;
import com.fundicion.lara.repository.UserRepository;
import com.fundicion.lara.utils.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.val;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;




@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final RateLimiter rateLimiter;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthResponse login(LoginRequest request) {
        String key = request.getEmail();

        if (!rateLimiter.isAllowed(key)) {
            throw new RuntimeException("Too many failed attempts. Try again later.");
        }

        var userEntity = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), userEntity.getPassword())) {
            rateLimiter.recordFailedAttempt(key);
            throw new NotFoundException("Invalid email or password");
        }

        rateLimiter.reset(key);

        String accessToken = jwtUtil.generateAccessToken(userEntity.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(userEntity.getEmail());
        long expiresIn = jwtUtil.getAccessTokenExpiration(); // ms

        UserDto userDto = modelMapper.map(userEntity, UserDto.class);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .user(userDto)
                .build();
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        var refresh = request.getRefreshToken();
        if (!jwtUtil.validateToken(refresh)) {
            throw new RuntimeException("Refresh token invalid or expired");
        }

        String subject = jwtUtil.extractUsername(refresh);
        String newAccess = jwtUtil.generateAccessToken(subject);
        long expiresIn = jwtUtil.getAccessTokenExpiration();

        // Optionally you can fetch user details
        var userEntity = userRepository.findByEmail(subject)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return AuthResponse.builder()
                .accessToken(newAccess)
                .refreshToken(refresh)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .user(modelMapper.map(userEntity, UserDto.class))
                .build();
    }

    public UserDto register(UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new RuntimeException("El correo ya está registrado.");
        }

        val encodedPassword = passwordEncoder.encode(userRequest.getPassword());

        val userEntity = UserEntity.builder()
                .name(userRequest.getName())
                .lastName(userRequest.getLastName())
                .motherLastName(userRequest.getMotherLastName())
                .email(userRequest.getEmail())
                .password(encodedPassword)
                .role(Role.PENDING)
                .build();

        val saved = userRepository.save(userEntity);
        return modelMapper.map(saved, UserDto.class);
    }
}
