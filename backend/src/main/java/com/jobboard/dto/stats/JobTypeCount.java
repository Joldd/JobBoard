package com.jobboard.dto.stats;

import com.jobboard.entity.enums.JobType;

public record JobTypeCount(JobType jobType, long count) {}
