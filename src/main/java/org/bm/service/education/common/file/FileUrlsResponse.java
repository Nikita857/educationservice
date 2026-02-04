package org.bm.service.education.common.file;

import java.util.Map;

public record FileUrlsResponse(
        Map<String, String> urls // path -> presigned URL
) {
}
