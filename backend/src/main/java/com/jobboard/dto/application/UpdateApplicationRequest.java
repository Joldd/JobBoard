package com.jobboard.dto.application;

import com.jobboard.entity.enums.JobType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.hibernate.validator.constraints.URL;

/**
 * Volontairement sans champ de statut : un changement de statut passe toujours par
 * PATCH /api/applications/{id}/status, jamais par cette mise à jour générale, pour
 * garantir qu'il génère systématiquement une ligne d'historique.
 */
public record UpdateApplicationRequest(
        @NotBlank @Size(max = 255) String company,
        @NotBlank @Size(max = 255) String position,
        @NotNull JobType jobType,
        @URL @Size(max = 2048) String jobOfferUrl,
        @NotNull LocalDate applicationDate,
        @DecimalMin(value = "0", inclusive = true) BigDecimal estimatedSalary,
        String notes) {}
