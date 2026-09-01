package com.jobboard.security;

import com.jobboard.exception.ErrorResponse;
import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * Sans configuration explicite, Spring Security renvoie un 403 brut (pas de JSON) dès
 * qu'une route protégée est appelée sans authentification valide. On remplace ce
 * comportement par un 401 JSON cohérent avec le reste des erreurs de l'API
 * (voir {@link com.jobboard.exception.GlobalExceptionHandler}).
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ErrorResponse body = ErrorResponse.of(HttpStatus.UNAUTHORIZED, "Authentification requise");
        objectMapper.writeValue(response.getWriter(), body);
    }
}
