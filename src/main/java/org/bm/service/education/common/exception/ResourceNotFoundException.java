package org.bm.service.education.common.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BaseException {
    public ResourceNotFoundException(String resourceName, Object id) {
        super(String.format("%s с идентификатором %s не найден", resourceName, id),
                HttpStatus.NOT_FOUND,
                "RESOURCE_NOT_FOUND");
    }
}
