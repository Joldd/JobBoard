package com.jobboard.repository;

import com.jobboard.entity.Application;
import com.jobboard.repository.projection.CompanyCountProjection;
import com.jobboard.repository.projection.JobTypeCountProjection;
import com.jobboard.repository.projection.StatusCountProjection;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ApplicationRepository extends JpaRepository<Application, Long>, JpaSpecificationExecutor<Application> {

    /**
     * Toute lecture/écriture d'une candidature passe par ce filtre user_id, jamais par
     * findById seul : ça empêche un utilisateur d'accéder aux candidatures d'un autre
     * simplement en devinant un id (IDOR), sans avoir à faire de vérification séparée
     * après coup dans le service.
     */
    Optional<Application> findByIdAndUserId(Long id, Long userId);

    @Query("select a.currentStatus as status, count(a) as count from Application a "
            + "where a.user.id = :userId group by a.currentStatus")
    List<StatusCountProjection> countGroupedByStatus(@Param("userId") Long userId);

    @Query("select a.jobType as jobType, count(a) as count from Application a "
            + "where a.user.id = :userId group by a.jobType")
    List<JobTypeCountProjection> countGroupedByJobType(@Param("userId") Long userId);

    @Query("select a.company as company, count(a) as count from Application a "
            + "where a.user.id = :userId group by a.company order by count(a) desc")
    List<CompanyCountProjection> countGroupedByCompany(@Param("userId") Long userId, Pageable pageable);

    @Query("select a.applicationDate from Application a where a.user.id = :userId")
    List<LocalDate> findAllApplicationDates(@Param("userId") Long userId);
}
