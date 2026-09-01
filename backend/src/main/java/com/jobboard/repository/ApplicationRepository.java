package com.jobboard.repository;

import com.jobboard.entity.Application;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ApplicationRepository extends JpaRepository<Application, Long>, JpaSpecificationExecutor<Application> {

    /**
     * Toute lecture/écriture d'une candidature passe par ce filtre user_id, jamais par
     * findById seul : ça empêche un utilisateur d'accéder aux candidatures d'un autre
     * simplement en devinant un id (IDOR), sans avoir à faire de vérification séparée
     * après coup dans le service.
     */
    Optional<Application> findByIdAndUserId(Long id, Long userId);
}
