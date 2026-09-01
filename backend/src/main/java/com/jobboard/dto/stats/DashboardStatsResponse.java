package com.jobboard.dto.stats;

import java.util.List;

/** {@code averageResponseDays} est {@code null} tant qu'aucune candidature n'a reçu de réponse. */
public record DashboardStatsResponse(
        List<StatusCount> byStatus,
        List<JobTypeCount> byJobType,
        List<CompanyCount> topCompanies,
        List<ConversionStep> conversionFunnel,
        Double averageResponseDays,
        List<MonthlyCount> applicationsOverTime) {}
