package com.jobboard.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Génère et valide les JWT signés HMAC-SHA256. Le token porte le strict nécessaire
 * (email en subject, id et nom en claims custom) : pas de rôle/permission puisque
 * l'application n'en a pas la notion pour l'instant.
 *
 * <p>Choix assumé : un seul token d'accès avec une expiration de 24h (configurable),
 * pas de refresh token. Pour un usage personnel, se reconnecter après expiration est
 * un compromis acceptable ; un flux de refresh token serait l'étape naturelle si
 * l'appli devait un jour rester connectée plus longtemps sans réauthentification.
 */
@Component
public class JwtService {

    private final SecretKey key;
    private final long expirationMs;

    public JwtService(
            @Value("${jwt.secret}") String base64Secret,
            @Value("${jwt.expiration-ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(java.util.Base64.getDecoder().decode(base64Secret));
        this.expirationMs = expirationMs;
    }

    public String generateToken(SecurityUser securityUser) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(securityUser.getUsername())
                .claim("uid", securityUser.getId())
                .claim("name", securityUser.getUser().getName())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMs)))
                .signWith(key)
                .compact();
    }

    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String email = extractEmail(token);
        return email.equals(userDetails.getUsername()) && !isExpired(token);
    }

    private boolean isExpired(String token) {
        return parseClaims(token).getExpiration().before(new Date());
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
