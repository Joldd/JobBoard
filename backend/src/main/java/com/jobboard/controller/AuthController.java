package com.jobboard.controller;

import com.jobboard.dto.auth.AuthResponse;
import com.jobboard.dto.auth.LoginRequest;
import com.jobboard.dto.auth.RegisterRequest;
import com.jobboard.dto.auth.UserResponse;
import com.jobboard.security.SecurityUser;
import com.jobboard.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    /** Route protégée simple : confirme qu'un token valide authentifie bien la requête. */
    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal SecurityUser securityUser) {
        return new UserResponse(securityUser.getUser().getEmail(), securityUser.getUser().getName());
    }
}
