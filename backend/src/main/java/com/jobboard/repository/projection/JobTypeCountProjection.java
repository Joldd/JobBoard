package com.jobboard.repository.projection;

import com.jobboard.entity.enums.JobType;

public interface JobTypeCountProjection {
    JobType getJobType();

    long getCount();
}
