CREATE TABLE answer_options
(
    id          UUID    NOT NULL,
    version     BIGINT,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    text        TEXT    NOT NULL,
    is_correct  BOOLEAN NOT NULL,
    order_index INTEGER NOT NULL,
    question_id UUID    NOT NULL,
    CONSTRAINT pk_answer_options PRIMARY KEY (id)
);

CREATE TABLE compliance_records
(
    id             UUID         NOT NULL,
    version        BIGINT,
    created_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    user_id        UUID         NOT NULL,
    requirement_id UUID         NOT NULL,
    status         VARCHAR(255) NOT NULL,
    completed_at   TIMESTAMP WITHOUT TIME ZONE,
    expires_at     TIMESTAMP WITHOUT TIME ZONE,
    enrollment_id  UUID,
    CONSTRAINT pk_compliance_records PRIMARY KEY (id)
);

CREATE TABLE courses
(
    id                         UUID         NOT NULL,
    version                    BIGINT,
    created_at                 TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at                 TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    title                      VARCHAR(255) NOT NULL,
    description                TEXT,
    thumbnail_url              VARCHAR(255),
    is_published               BOOLEAN      NOT NULL,
    estimated_duration_minutes INTEGER,
    author_id                  UUID,
    CONSTRAINT pk_courses PRIMARY KEY (id)
);

CREATE TABLE enrollments
(
    id               UUID         NOT NULL,
    version          BIGINT,
    created_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    user_id          UUID         NOT NULL,
    course_id        UUID         NOT NULL,
    status           VARCHAR(255) NOT NULL,
    enrolled_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    completed_at     TIMESTAMP WITHOUT TIME ZONE,
    progress_percent INTEGER      NOT NULL,
    CONSTRAINT pk_enrollments PRIMARY KEY (id)
);

CREATE TABLE lesson_progress
(
    id            UUID    NOT NULL,
    version       BIGINT,
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    enrollment_id UUID    NOT NULL,
    lesson_id     UUID    NOT NULL,
    is_completed  BOOLEAN NOT NULL,
    completed_at  TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_lesson_progress PRIMARY KEY (id)
);

CREATE TABLE lessons
(
    id          UUID         NOT NULL,
    version     BIGINT,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    title       VARCHAR(255) NOT NULL,
    content     JSONB,
    video_url   VARCHAR(255),
    order_index INTEGER      NOT NULL,
    module_id   UUID         NOT NULL,
    CONSTRAINT pk_lessons PRIMARY KEY (id)
);

CREATE TABLE modules
(
    id          UUID         NOT NULL,
    version     BIGINT,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    order_index INTEGER      NOT NULL,
    course_id   UUID         NOT NULL,
    CONSTRAINT pk_modules PRIMARY KEY (id)
);

CREATE TABLE organization_units
(
    id         UUID         NOT NULL,
    version    BIGINT,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    name       VARCHAR(255) NOT NULL,
    code       VARCHAR(255) NOT NULL,
    parent_id  UUID,
    CONSTRAINT pk_organization_units PRIMARY KEY (id)
);

CREATE TABLE positions
(
    id         UUID         NOT NULL,
    version    BIGINT,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    name       VARCHAR(255) NOT NULL,
    code       VARCHAR(255) NOT NULL,
    CONSTRAINT pk_positions PRIMARY KEY (id)
);

CREATE TABLE questions
(
    id          UUID    NOT NULL,
    version     BIGINT,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    text        TEXT    NOT NULL,
    order_index INTEGER NOT NULL,
    points      INTEGER NOT NULL,
    test_id     UUID    NOT NULL,
    CONSTRAINT pk_questions PRIMARY KEY (id)
);

CREATE TABLE test_attempts
(
    id          UUID    NOT NULL,
    version     BIGINT,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    user_id     UUID    NOT NULL,
    test_id     UUID    NOT NULL,
    started_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    finished_at TIMESTAMP WITHOUT TIME ZONE,
    score       INTEGER,
    is_passed   BOOLEAN NOT NULL,
    CONSTRAINT pk_test_attempts PRIMARY KEY (id)
);

CREATE TABLE tests
(
    id                 UUID         NOT NULL,
    version            BIGINT,
    created_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    title              VARCHAR(255) NOT NULL,
    description        TEXT,
    passing_score      INTEGER      NOT NULL,
    time_limit_minutes INTEGER,
    max_attempts       INTEGER      NOT NULL,
    lesson_id          UUID         NOT NULL,
    CONSTRAINT pk_tests PRIMARY KEY (id)
);

CREATE TABLE training_requirements
(
    id                          UUID         NOT NULL,
    version                     BIGINT,
    created_at                  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at                  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    name                        VARCHAR(255) NOT NULL,
    description                 TEXT,
    course_id                   UUID,
    scope_type                  VARCHAR(255) NOT NULL,
    target_position_id          UUID,
    target_organization_unit_id UUID,
    validity_period_days        INTEGER,
    is_mandatory                BOOLEAN      NOT NULL,
    CONSTRAINT pk_training_requirements PRIMARY KEY (id)
);

CREATE TABLE user_assignments
(
    id                   UUID    NOT NULL,
    version              BIGINT,
    created_at           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    user_id              UUID    NOT NULL,
    position_id          UUID    NOT NULL,
    organization_unit_id UUID    NOT NULL,
    start_date           date    NOT NULL,
    end_date             date,
    is_primary           BOOLEAN NOT NULL,
    CONSTRAINT pk_user_assignments PRIMARY KEY (id)
);

CREATE TABLE users
(
    id                    UUID         NOT NULL,
    version               BIGINT,
    created_at            TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at            TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    username              VARCHAR(255) NOT NULL,
    first_name            VARCHAR(255) NOT NULL,
    last_name             VARCHAR(255) NOT NULL,
    middle_name           VARCHAR(255),
    active                BOOLEAN      NOT NULL,
    email                 VARCHAR(255) NOT NULL,
    personnel_number      VARCHAR(255) NOT NULL,
    password_hash         VARCHAR(255) NOT NULL,
    role                  VARCHAR(255) NOT NULL,
    status                VARCHAR(255) NOT NULL,
    current_assignment_id UUID,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE lesson_progress
    ADD CONSTRAINT uc_4ad5ea004337d3100adecc68f UNIQUE (enrollment_id, lesson_id);

ALTER TABLE enrollments
    ADD CONSTRAINT uc_6c8d6aa5e27c938c0b7ce5c28 UNIQUE (user_id, course_id);

ALTER TABLE organization_units
    ADD CONSTRAINT uc_organization_units_code UNIQUE (code);

ALTER TABLE positions
    ADD CONSTRAINT uc_positions_code UNIQUE (code);

ALTER TABLE tests
    ADD CONSTRAINT uc_tests_lesson UNIQUE (lesson_id);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT uc_users_personnelnumber UNIQUE (personnel_number);

ALTER TABLE users
    ADD CONSTRAINT uc_users_username UNIQUE (username);

CREATE INDEX idx_compliance_records_expires_at ON compliance_records (expires_at);

CREATE INDEX idx_compliance_records_status ON compliance_records (status);

CREATE INDEX idx_courses_published ON courses (is_published);

CREATE INDEX idx_enrollments_status ON enrollments (status);

CREATE INDEX idx_org_units_code ON organization_units (code);

CREATE INDEX idx_positions_code ON positions (code);

CREATE INDEX idx_test_attempts_user_test ON test_attempts (user_id, test_id);

CREATE INDEX idx_training_req_scope ON training_requirements (scope_type);

CREATE INDEX idx_users_email ON users (email);

CREATE INDEX idx_users_personnel_number ON users (personnel_number);

CREATE INDEX idx_users_role ON users (role);

CREATE INDEX idx_users_status ON users (status);

ALTER TABLE answer_options
    ADD CONSTRAINT FK_ANSWER_OPTIONS_ON_QUESTION FOREIGN KEY (question_id) REFERENCES questions (id);

CREATE INDEX idx_answer_options_question ON answer_options (question_id);

ALTER TABLE compliance_records
    ADD CONSTRAINT FK_COMPLIANCE_RECORDS_ON_ENROLLMENT FOREIGN KEY (enrollment_id) REFERENCES enrollments (id);

ALTER TABLE compliance_records
    ADD CONSTRAINT FK_COMPLIANCE_RECORDS_ON_REQUIREMENT FOREIGN KEY (requirement_id) REFERENCES training_requirements (id);

CREATE INDEX idx_compliance_records_requirement ON compliance_records (requirement_id);

ALTER TABLE compliance_records
    ADD CONSTRAINT FK_COMPLIANCE_RECORDS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX idx_compliance_records_user ON compliance_records (user_id);

ALTER TABLE courses
    ADD CONSTRAINT FK_COURSES_ON_AUTHOR FOREIGN KEY (author_id) REFERENCES users (id);

CREATE INDEX idx_courses_author ON courses (author_id);

ALTER TABLE enrollments
    ADD CONSTRAINT FK_ENROLLMENTS_ON_COURSE FOREIGN KEY (course_id) REFERENCES courses (id);

CREATE INDEX idx_enrollments_course ON enrollments (course_id);

ALTER TABLE enrollments
    ADD CONSTRAINT FK_ENROLLMENTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX idx_enrollments_user ON enrollments (user_id);

ALTER TABLE lessons
    ADD CONSTRAINT FK_LESSONS_ON_MODULE FOREIGN KEY (module_id) REFERENCES modules (id);

CREATE INDEX idx_lessons_module ON lessons (module_id);

ALTER TABLE lesson_progress
    ADD CONSTRAINT FK_LESSON_PROGRESS_ON_ENROLLMENT FOREIGN KEY (enrollment_id) REFERENCES enrollments (id);

CREATE INDEX idx_lesson_progress_enrollment ON lesson_progress (enrollment_id);

ALTER TABLE lesson_progress
    ADD CONSTRAINT FK_LESSON_PROGRESS_ON_LESSON FOREIGN KEY (lesson_id) REFERENCES lessons (id);

CREATE INDEX idx_lesson_progress_lesson ON lesson_progress (lesson_id);

ALTER TABLE modules
    ADD CONSTRAINT FK_MODULES_ON_COURSE FOREIGN KEY (course_id) REFERENCES courses (id);

CREATE INDEX idx_modules_course ON modules (course_id);

ALTER TABLE organization_units
    ADD CONSTRAINT FK_ORGANIZATION_UNITS_ON_PARENT FOREIGN KEY (parent_id) REFERENCES organization_units (id);

CREATE INDEX idx_org_units_parent ON organization_units (parent_id);

ALTER TABLE questions
    ADD CONSTRAINT FK_QUESTIONS_ON_TEST FOREIGN KEY (test_id) REFERENCES tests (id);

CREATE INDEX idx_questions_test ON questions (test_id);

ALTER TABLE tests
    ADD CONSTRAINT FK_TESTS_ON_LESSON FOREIGN KEY (lesson_id) REFERENCES lessons (id);

CREATE INDEX idx_tests_lesson ON tests (lesson_id);

ALTER TABLE test_attempts
    ADD CONSTRAINT FK_TEST_ATTEMPTS_ON_TEST FOREIGN KEY (test_id) REFERENCES tests (id);

CREATE INDEX idx_test_attempts_test ON test_attempts (test_id);

ALTER TABLE test_attempts
    ADD CONSTRAINT FK_TEST_ATTEMPTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX idx_test_attempts_user ON test_attempts (user_id);

ALTER TABLE training_requirements
    ADD CONSTRAINT FK_TRAINING_REQUIREMENTS_ON_COURSE FOREIGN KEY (course_id) REFERENCES courses (id);

CREATE INDEX idx_training_req_course ON training_requirements (course_id);

ALTER TABLE training_requirements
    ADD CONSTRAINT FK_TRAINING_REQUIREMENTS_ON_TARGET_ORGANIZATION_UNIT FOREIGN KEY (target_organization_unit_id) REFERENCES organization_units (id);

CREATE INDEX idx_training_req_org_unit ON training_requirements (target_organization_unit_id);

ALTER TABLE training_requirements
    ADD CONSTRAINT FK_TRAINING_REQUIREMENTS_ON_TARGET_POSITION FOREIGN KEY (target_position_id) REFERENCES positions (id);

CREATE INDEX idx_training_req_position ON training_requirements (target_position_id);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_CURRENT_ASSIGNMENT FOREIGN KEY (current_assignment_id) REFERENCES user_assignments (id);

ALTER TABLE user_assignments
    ADD CONSTRAINT FK_USER_ASSIGNMENTS_ON_ORGANIZATION_UNIT FOREIGN KEY (organization_unit_id) REFERENCES organization_units (id);

CREATE INDEX idx_user_assignments_org_unit ON user_assignments (organization_unit_id);

ALTER TABLE user_assignments
    ADD CONSTRAINT FK_USER_ASSIGNMENTS_ON_POSITION FOREIGN KEY (position_id) REFERENCES positions (id);

CREATE INDEX idx_user_assignments_position ON user_assignments (position_id);

ALTER TABLE user_assignments
    ADD CONSTRAINT FK_USER_ASSIGNMENTS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX idx_user_assignments_user ON user_assignments (user_id);