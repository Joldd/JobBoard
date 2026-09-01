package com.jobboard.dto.application;

import com.jobboard.entity.enums.ApplicationStatus;
import java.time.LocalDateTime;

public record StatusHistoryResponse(Long id, ApplicationStatus status, LocalDateTime changedAt, String comment) {}
