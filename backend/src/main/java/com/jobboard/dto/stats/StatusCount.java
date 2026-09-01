package com.jobboard.dto.stats;

import com.jobboard.entity.enums.ApplicationStatus;

public record StatusCount(ApplicationStatus status, long count) {}
