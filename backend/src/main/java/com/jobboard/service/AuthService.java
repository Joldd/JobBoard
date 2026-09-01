package com.jobboard.service;

import com.jobboard.dto.auth.AuthResponse;
import com.jobboard.dto.auth.LoginRequest;
import com.jobboard.dto.auth.RegisterRequest;
import com.jobboard.entity.User;
import com.jobboard.exception.DuplicateEmailException;
import com.jobboard.repository.UserRepository;
import com.jobboard.security.JwtService;
import com.jobboard.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException(request.email());
        }

        User user = User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .name(request.name())
                .build();
        userRepository.save(user);

        String token = jwtService.generateToken(new SecurityUser(user));
        return new AuthResponse(token, user.getEmail(), user.getName());
    }

    public AuthResponse login(LoginRequest request) {
        // Délègue à l'AuthenticationProvider (DaoAuthenticationProvider) configuré dans
        // SecurityConfig : il charge l'utilisateur via CustomUserDetailsService et vérifie
        // le mot de passe avec le PasswordEncoder. Lève BadCredentialsException si invalide
        // (traduit en 401 par GlobalExceptionHandler).
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        String token = jwtService.generateToken(securityUser);
        return new AuthResponse(token, securityUser.getUser().getEmail(), securityUser.getUser().getName());
    }
}
