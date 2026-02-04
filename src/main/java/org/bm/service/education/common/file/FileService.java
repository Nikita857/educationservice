package org.bm.service.education.common.file;

import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bm.service.education.common.config.MinioProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    /**
     * Загрузить файл в MinIO
     * 
     * @param file     загружаемый файл
     * @param fileType тип файла (определяет бакет)
     * @param folder   папка внутри бакета
     * @return полный путь: bucket:folder/filename
     */
    public String uploadFile(MultipartFile file, FileType fileType, String folder) {
        String bucket = getBucketForType(fileType);
        String objectName = generateFileName(file.getOriginalFilename(), folder);

        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());

            log.info("Uploaded file: {}:{}", bucket, objectName);
            // Возвращаем путь в формате bucket:objectName для однозначной идентификации
            return bucket + ":" + objectName;

        } catch (Exception e) {
            log.error("Failed to upload file: {}", e.getMessage());
            throw new RuntimeException("Ошибка при загрузке файла", e);
        }
    }

    /**
     * Загрузить файл с автоопределением типа по content-type
     */
    public String uploadFile(MultipartFile file, String folder) {
        FileType fileType = detectFileType(file.getContentType());
        return uploadFile(file, fileType, folder);
    }

    /**
     * Получить presigned URL для доступа к файлу
     * 
     * @param filePath      путь в формате bucket:objectName
     * @param expiryMinutes время действия ссылки
     * @return presigned URL
     */
    public String getPresignedUrl(String filePath, int expiryMinutes) {
        String[] parts = parseFilePath(filePath);
        String bucket = parts[0];
        String objectName = parts[1];

        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .method(Method.GET)
                            .expiry(expiryMinutes, TimeUnit.MINUTES)
                            .build());
        } catch (Exception e) {
            log.error("Failed to generate presigned URL: {}", e.getMessage());
            throw new RuntimeException("Ошибка при генерации ссылки на файл", e);
        }
    }

    /**
     * Удалить файл из MinIO
     * 
     * @param filePath путь в формате bucket:objectName
     */
    public void deleteFile(String filePath) {
        String[] parts = parseFilePath(filePath);
        String bucket = parts[0];
        String objectName = parts[1];

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .build());
            log.info("Deleted file: {}:{}", bucket, objectName);
        } catch (Exception e) {
            log.error("Failed to delete file: {}", e.getMessage());
            throw new RuntimeException("Ошибка при удалении файла", e);
        }
    }

    /**
     * Проверить существование файла
     * 
     * @param filePath путь в формате bucket:objectName
     */
    public boolean fileExists(String filePath) {
        String[] parts = parseFilePath(filePath);
        String bucket = parts[0];
        String objectName = parts[1];

        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .build());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // === Private helpers ===

    private String getBucketForType(FileType fileType) {
        return switch (fileType) {
            case VIDEO -> minioProperties.getVideoBucket();
            case IMAGE -> minioProperties.getImageBucket();
            case ATTACHMENT -> minioProperties.getAttachmentBucket();
        };
    }

    private FileType detectFileType(String contentType) {
        if (contentType == null) {
            return FileType.ATTACHMENT;
        }
        if (contentType.startsWith("video/")) {
            return FileType.VIDEO;
        }
        if (contentType.startsWith("image/")) {
            return FileType.IMAGE;
        }
        return FileType.ATTACHMENT;
    }

    private String[] parseFilePath(String filePath) {
        if (filePath == null || !filePath.contains(":")) {
            throw new IllegalArgumentException("Неверный формат пути. Ожидается: bucket:objectName");
        }
        int colonIndex = filePath.indexOf(":");
        return new String[] {
                filePath.substring(0, colonIndex),
                filePath.substring(colonIndex + 1)
        };
    }

    private String generateFileName(String originalName, String folder) {
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }
        String prefix = folder != null && !folder.isEmpty() ? folder + "/" : "";
        return prefix + UUID.randomUUID() + extension;
    }
}
