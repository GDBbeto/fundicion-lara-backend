package com.fundicion.lara.controller;

import com.fundicion.lara.commons.data.ApiResponse;
import com.fundicion.lara.dto.UserDto;
import com.fundicion.lara.dto.request.UserRequest;
import com.fundicion.lara.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AUTH")
@RestController
@AllArgsConstructor
@RequestMapping(value = "v1/public/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @Operation(
            operationId = "registerUser",
            summary = "Register a new user (no authentication required)",
            description = "Creates a new user account. The password should already be hashed (SHA-256) by the frontend before sending."
    )
    public ApiResponse<UserDto> register(@RequestBody UserRequest userRequest) {
        return ApiResponse.ok(authService.register(userRequest));
    }
}
