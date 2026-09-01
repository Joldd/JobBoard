package com.jobboard.dto.application;

import com.jobboard.entity.enums.ApplicationStatus;
import com.jobboard.entity.enums.JobType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.hibernate.validator.constraints.URL;

/**
 * {@code initialStatus} est optionnel : par défaut TO_APPLY (voir ApplicationService),
 * mais permet de journaliser une candidature déjà en cours (ex: import d'un suivi
 * existant) sans devoir enchaîner un appel à /status juste après la création.
 *
 * {@code initialStatusChangedAt} est optionnel aussi : par défaut "maintenant", mais
 * permet d'antidater la toute première ligne d'historique quand on saisit après coup
 * une candidature déjà en cours depuis un moment — sans quoi le délai moyen de réponse
 * calculé au dashboard serait faussé (l'historique daterait la candidature du jour de
 * la saisie, pas du jour réel de la candidature).
 */
public record CreateApplicationRequest(
        @NotBlank @Size(max = 255) String company,
        @NotBlank @Size(max = 255) String position,
        @NotNull JobType jobType,
        @URL @Size(max = 2048) String jobOfferUrl,
        @NotNull LocalDate applicationDate,
        ApplicationStatus initialStatus,
        LocalDateTime initialStatusChangedAt,
        @DecimalMin(value = "0", inclusive = true) BigDecimal estimatedSalary,
        String notes) {}
