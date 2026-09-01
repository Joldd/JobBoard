package com.jobboard.dto.stats;

/** {@code month} au format ISO "yyyy-MM" (ex: "2026-08"), trié chronologiquement. */
public record MonthlyCount(String month, long count) {}
