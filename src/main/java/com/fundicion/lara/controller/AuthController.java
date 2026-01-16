package com.fundicion.lara.controller;

import com.fundicion.lara.commons.data.ApiResponse;
import com.fundicion.lara.dto.UserDto;
import com.fundicion.lara.dto.request.LoginRequest;
import com.fundicion.lara.dto.request.RefreshTokenRequest;
import com.fundicion.lara.dto.request.UserRequest;
import com.fundicion.lara.dto.response.AuthResponse;
import com.fundicion.lara.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AUTH")
@RestController
@AllArgsConstructor
@RequestMapping(value = "v1/public/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
     @Operation(
            operationId = "loginUser",
            summary = "Authenticate user and return tokens",
            description = "Logs in a user using email and password, returning an access token and a refresh token."
    )
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    @Operation(
            operationId = "refreshToken",
            summary = "Refresh JWT access token",
            description = "Refreshes the access token using a valid refresh token."
    )
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/register")
    @Operation(
            operationId = "registerUser",
            summary = "Register a new user (no authentication required)",
            description = "Creates a new user account. The password should already be hashed (SHA-256) by the frontend before sending."
    )
    public ApiResponse<UserDto> register(@RequestBody UserRequest userRequest) {
        return ApiResponse.ok(authService.register(userRequest));
    }

    @GetMapping("/status")
    @Operation(
            operationId = "status",
            summary = "status",
            description = "status"
    )
    public ApiResponse<String> status() {
        return ApiResponse.ok("OK");
    }
}
