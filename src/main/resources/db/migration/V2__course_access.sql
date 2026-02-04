-- Заявки на доступ к курсам
CREATE TABLE course_access_requests (
    id UUID PRIMARY KEY,
    version BIGINT,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    course_id UUID NOT NULL,
    user_id UUID NOT NULL,
    requested_by_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    reason TEXT,
    resolved_by_id UUID,
    resolved_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT fk_access_req_course FOREIGN KEY (course_id) REFERENCES courses(id),
    CONSTRAINT fk_access_req_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_access_req_requested_by FOREIGN KEY (requested_by_id) REFERENCES users(id),
    CONSTRAINT fk_access_req_resolved_by FOREIGN KEY (resolved_by_id) REFERENCES users(id)
);

CREATE INDEX idx_access_req_course ON course_access_requests(course_id);
CREATE INDEX idx_access_req_user ON course_access_requests(user_id);
CREATE INDEX idx_access_req_status ON course_access_requests(status);

-- Факт доступа к курсу
CREATE TABLE course_access (
    id UUID PRIMARY KEY,
    version BIGINT,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    course_id UUID NOT NULL,
    user_id UUID NOT NULL,
    granted_by_id UUID NOT NULL,
    expires_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT fk_course_access_course FOREIGN KEY (course_id) REFERENCES courses(id),
    CONSTRAINT fk_course_access_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_course_access_granted_by FOREIGN KEY (granted_by_id) REFERENCES users(id),
    CONSTRAINT uc_course_access_user_course UNIQUE (course_id, user_id)
);

CREATE INDEX idx_course_access_course ON course_access(course_id);
CREATE INDEX idx_course_access_user ON course_access(user_id);
