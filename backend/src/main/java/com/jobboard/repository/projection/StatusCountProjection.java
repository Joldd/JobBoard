package com.jobboard.repository.projection;

import com.jobboard.entity.enums.ApplicationStatus;

/** Projection d'interface Spring Data : les noms des accesseurs doivent matcher les alias JPQL. */
public interface StatusCountProjection {
    ApplicationStatus getStatus();

    long getCount();
}
