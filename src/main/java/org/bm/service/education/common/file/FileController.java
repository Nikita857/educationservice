package org.bm.service.education.common.file;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bm.service.education.common.api.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Tag(name = "Files", description = "Работа с файлами")
public class FileController {

    private final FileService fileService;

    private static final int DEFAULT_URL_EXPIRY_MINUTES = 120;

    @Operation(summary = "Получить presigned URL для списка файлов", description = "Возвращает свежие presigned URL для доступа к файлам. Путь в формате bucket:objectName")
    @PostMapping("/urls")
    public ApiResponse<FileUrlsResponse> getFileUrls(@Valid @RequestBody GetFileUrlsRequest request) {
        Map<String, String> urls = new HashMap<>();

        for (String path : request.paths()) {
            if (fileService.fileExists(path)) {
                urls.put(path, fileService.getPresignedUrl(path, DEFAULT_URL_EXPIRY_MINUTES));
            } else {
                urls.put(path, null);
            }
        }

        return ApiResponse.ok(new FileUrlsResponse(urls), "/api/v1/files/urls");
    }

    @Operation(summary = "Получить presigned URL для одного файла")
    @GetMapping("/url")
    public ApiResponse<String> getFileUrl(
            @Parameter(description = "Путь к файлу (bucket:objectName)") @RequestParam String path,
            @Parameter(description = "Время действия URL в минутах") @RequestParam(defaultValue = "120") int expiryMinutes) {
        if (!fileService.fileExists(path)) {
            throw new RuntimeException("Файл не найден: " + path);
        }

        String url = fileService.getPresignedUrl(path, expiryMinutes);
        return ApiResponse.ok(url, "/api/v1/files/url");
    }

    @Operation(summary = "Загрузить файл", description = "Автоматически определяет бакет по content-type файла. Возвращает путь в формате bucket:objectName")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> uploadFile(
            @Parameter(description = "Файл для загрузки") @RequestParam("file") MultipartFile file,
            @Parameter(description = "Папка внутри бакета") @RequestParam(required = false) String folder) {

        String path = fileService.uploadFile(file, folder);
        return ApiResponse.created(path, "/api/v1/files/upload");
    }

    @Operation(summary = "Загрузить файл с явным указанием типа")
    @PostMapping(value = "/upload/{fileType}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> uploadFileWithType(
            @Parameter(description = "Файл для загрузки") @RequestParam("file") MultipartFile file,
            @Parameter(description = "Тип файла (VIDEO, IMAGE, ATTACHMENT)") @PathVariable FileType fileType,
            @Parameter(description = "Папка внутри бакета") @RequestParam(required = false) String folder) {

        String path = fileService.uploadFile(file, fileType, folder);
        return ApiResponse.created(path, "/api/v1/files/upload/" + fileType);
    }

    @Operation(summary = "Удалить файл")
    @DeleteMapping()
    public ApiResponse<Void> deleteFile(
            @Parameter(description = "Путь к файлу (bucket:objectName)") @RequestParam String path) {

        fileService.deleteFile(path);
        return ApiResponse.ok(null, "/api/v1/files");
    }
}
