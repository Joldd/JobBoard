-- JobBoard - schéma initial

CREATE TABLE users (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    name            VARCHAR(255) NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE applications (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id             BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    company             VARCHAR(255) NOT NULL,
    position            VARCHAR(255) NOT NULL,
    job_type            VARCHAR(20) NOT NULL
                         CHECK (job_type IN ('CDI', 'CDD', 'INTERNSHIP', 'ALTERNANCE', 'FREELANCE')),
    job_offer_url       VARCHAR(2048),
    application_date    DATE NOT NULL,
    current_status      VARCHAR(30) NOT NULL DEFAULT 'TO_APPLY'
                         CHECK (current_status IN
                             ('TO_APPLY', 'APPLIED', 'FOLLOW_UP', 'HR_INTERVIEW',
                              'TECHNICAL_INTERVIEW', 'OFFER', 'REJECTED', 'WITHDRAWN')),
    estimated_salary    NUMERIC(10, 2),
    notes               TEXT,
    created_at          TIMESTAMP NOT NULL DEFAULT now(),
    updated_at          TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_applications_user_id          ON applications(user_id);
CREATE INDEX idx_applications_current_status   ON applications(current_status);
CREATE INDEX idx_applications_application_date ON applications(application_date);

CREATE TABLE status_history (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    application_id  BIGINT NOT NULL REFERENCES applications(id) ON DELETE CASCADE,
    status          VARCHAR(30) NOT NULL
                    CHECK (status IN
                        ('TO_APPLY', 'APPLIED', 'FOLLOW_UP', 'HR_INTERVIEW',
                         'TECHNICAL_INTERVIEW', 'OFFER', 'REJECTED', 'WITHDRAWN')),
    changed_at      TIMESTAMP NOT NULL DEFAULT now(),
    comment         TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_status_history_application_id ON status_history(application_id);
CREATE INDEX idx_status_history_changed_at     ON status_history(changed_at);

CREATE TABLE contacts (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    application_id  BIGINT NOT NULL REFERENCES applications(id) ON DELETE CASCADE,
    name            VARCHAR(255) NOT NULL,
    role            VARCHAR(255),
    email           VARCHAR(255),
    phone           VARCHAR(30),
    created_at      TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_contacts_application_id ON contacts(application_id);
