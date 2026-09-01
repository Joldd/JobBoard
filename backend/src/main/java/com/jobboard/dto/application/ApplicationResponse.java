package com.jobboard.dto.application;

import com.jobboard.entity.enums.ApplicationStatus;
import com.jobboard.entity.enums.JobType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ApplicationResponse(
        Long id,
        String company,
        String position,
        JobType jobType,
        String jobOfferUrl,
        LocalDate applicationDate,
        ApplicationStatus currentStatus,
        BigDecimal estimatedSalary,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {}
