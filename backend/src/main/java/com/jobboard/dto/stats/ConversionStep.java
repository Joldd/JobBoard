package com.jobboard.dto.stats;

import com.jobboard.entity.enums.ApplicationStatus;

/**
 * {@code fromCount}/{@code toCount} : nombre de candidatures dont l'historique contient
 * au moins une fois {@code fromStage}, respectivement {@code toStage} (peu importe l'ordre
 * réel des statuts traversés). {@code conversionRate} = toCount / fromCount, 0 si fromCount
 * vaut 0.
 */
public record ConversionStep(
        ApplicationStatus fromStage, ApplicationStatus toStage, long fromCount, long toCount, double conversionRate) {}
