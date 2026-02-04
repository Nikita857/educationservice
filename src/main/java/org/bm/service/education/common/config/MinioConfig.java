package org.bm.service.education.common.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class MinioConfig {

    private final MinioProperties minioProperties;

    @Bean
    public MinioClient minioClient() {
        MinioClient client = MinioClient.builder()
                .endpoint(minioProperties.getEndpoint())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();

        // Создаем все бакеты если не существуют
        List<String> buckets = List.of(
                minioProperties.getVideoBucket(),
                minioProperties.getImageBucket(),
                minioProperties.getAttachmentBucket());

        for (String bucket : buckets) {
            ensureBucketExists(client, bucket);
        }

        return client;
    }

    private void ensureBucketExists(MinioClient client, String bucket) {
        try {
            boolean exists = client.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucket)
                            .build());

            if (!exists) {
                client.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucket)
                                .build());
                log.info("Created MinIO bucket: {}", bucket);
            }
        } catch (Exception e) {
            log.warn("Could not check/create MinIO bucket '{}': {}", bucket, e.getMessage());
        }
    }
}
