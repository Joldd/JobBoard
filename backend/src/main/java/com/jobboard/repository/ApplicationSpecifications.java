package com.jobboard.repository;

import com.jobboard.entity.Application;
import com.jobboard.entity.enums.ApplicationStatus;
import com.jobboard.entity.enums.JobType;
import java.time.LocalDate;
import org.springframework.data.jpa.domain.Specification;

/**
 * Chaque filtre de la liste des candidatures (statut, type de poste, entreprise, période)
 * est optionnel et combinable librement. Une Specification par critère, combinées via
 * {@link Specification#allOf} (qui ignore silencieusement les éléments null) plutôt qu'une
 * requête dérivée par combinaison de filtres (explosion combinatoire) ou un grand @Query
 * avec des conditions "?1 IS NULL OR ..." (moins lisible, moins type-safe).
 */
public final class ApplicationSpecifications {

    private ApplicationSpecifications() {}

    public static Specification<Application> belongsToUser(Long userId) {
        return (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<Application> hasStatus(ApplicationStatus status) {
        return status == null ? null : (root, query, cb) -> cb.equal(root.get("currentStatus"), status);
    }

    public static Specification<Application> hasJobType(JobType jobType) {
        return jobType == null ? null : (root, query, cb) -> cb.equal(root.get("jobType"), jobType);
    }

    public static Specification<Application> companyContains(String company) {
        if (company == null || company.isBlank()) {
            return null;
        }
        String pattern = "%" + company.toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("company")), pattern);
    }

    public static Specification<Application> applicationDateBetween(LocalDate from, LocalDate to) {
        if (from == null && to == null) {
            return null;
        }
        if (from != null && to != null) {
            return (root, query, cb) -> cb.between(root.get("applicationDate"), from, to);
        }
        if (from != null) {
            return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("applicationDate"), from);
        }
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("applicationDate"), to);
    }
}
