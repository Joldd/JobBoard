package com.jobboard.repository;

import com.jobboard.entity.StatusHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StatusHistoryRepository extends JpaRepository<StatusHistory, Long> {

    List<StatusHistory> findByApplicationIdOrderByChangedAtDesc(Long applicationId);

    /**
     * Tout l'historique de toutes les candidatures de l'utilisateur, trié par candidature
     * puis chronologiquement : exactement l'ordre dont a besoin le calcul du funnel de
     * conversion et du délai moyen de réponse (StatsService). sh.getApplication().getId()
     * ne déclenche pas de requête supplémentaire malgré le fetch LAZY : l'id est déjà
     * connu depuis la colonne application_id chargée avec la ligne elle-même.
     */
    @Query("select sh from StatusHistory sh where sh.application.user.id = :userId "
            + "order by sh.application.id asc, sh.changedAt asc")
    List<StatusHistory> findAllForUserOrderedByApplicationAndTime(@Param("userId") Long userId);
}
