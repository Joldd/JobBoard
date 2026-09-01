package com.jobboard.entity.enums;

/**
 * Miroir exact des valeurs autorisées par la contrainte CHECK sur applications.current_status
 * et status_history.status. L'ordre des constantes suit le pipeline attendu, même si rien
 * n'empêche techniquement de sauter des étapes (ex: passer directement à REJECTED).
 */
public enum ApplicationStatus {
    TO_APPLY,
    APPLIED,
    FOLLOW_UP,
    HR_INTERVIEW,
    TECHNICAL_INTERVIEW,
    OFFER,
    REJECTED,
    WITHDRAWN
}
