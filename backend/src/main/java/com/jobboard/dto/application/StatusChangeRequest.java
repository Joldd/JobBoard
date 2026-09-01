package com.jobboard.dto.application;

import com.jobboard.entity.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * {@code changedAt} est optionnel : par défaut "maintenant", mais peut être antidaté
 * (ex: "j'ai reçu ce refus mardi dernier, j'oublie de le noter avant aujourd'hui") —
 * important pour que le délai moyen de réponse calculé au dashboard reste fiable.
 */
public record StatusChangeRequest(@NotNull ApplicationStatus status, String comment, LocalDateTime changedAt) {}
