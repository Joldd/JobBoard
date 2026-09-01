package com.jobboard.security;

import com.jobboard.entity.User;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Adapte notre entité {@link User} au contrat {@link UserDetails} attendu par Spring
 * Security, sans faire fuiter de dépendance Spring Security dans l'entité JPA elle-même.
 *
 * <p>Pas de notion de rôle pour l'instant (application mono-utilisateur par compte) :
 * {@link #getAuthorities()} renvoie une liste vide. Les méthodes isAccountNonExpired,
 * isEnabled, etc. ont des implémentations par défaut à {@code true} dans l'interface
 * {@link UserDetails} depuis Spring Security 5.7 — inutile de les redéfinir ici.
 */
@Getter
public class SecurityUser implements UserDetails {

    private final User user;

    public SecurityUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return user.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }
}
