package org.bm.service.education.common.file;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record GetFileUrlsRequest(
        @NotEmpty(message = "Список путей к файлам не может быть пустым") List<String> paths) {
}
