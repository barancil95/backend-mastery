package com.baranproject.backendmastery.service;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

/**
 * Dosya Depolama Servisi (S3 / MinIO API wrapper)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.endpoint}")
    private String minioEndpoint;

    @Value("${minio.public-url}")
    private String publicUrl;

    /**
     * Gelen dosyayı MinIO / S3 bucket'ına yükler ve erişilebilir URL'ini döner.
     */
    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            // 1. Bucket var mı kontrol et, yoksa oluştur (Auto-create bucket)
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                log.info("Bucket '{}' bulunamadı, yeni oluşturuluyor.", bucketName);
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());

                // Herkese açık okuma izni (Public Read-Only) veren JSON politikası
                String policyJson = "{\n" +
                        "  \"Version\": \"2012-10-17\",\n" +
                        "  \"Statement\": [\n" +
                        "    {\n" +
                        "      \"Effect\": \"Allow\",\n" +
                        "      \"Principal\": \"*\",\n" +
                        "      \"Action\": [\"s3:GetObject\"],\n" +
                        "      \"Resource\": [\"arn:aws:s3:::" + bucketName + "/*\"]\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}";

                minioClient.setBucketPolicy(
                        io.minio.SetBucketPolicyArgs.builder()
                                .bucket(bucketName)
                                .config(policyJson)
                                .build()
                );
                log.info("Bucket '{}' için herkese açık okuma politikası tanımlandı.", bucketName);
            }

            // 2. Çakışmayı önlemek için benzersiz bir dosya adı üret (UUID + orijinal uzantı)
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String objectName = UUID.randomUUID().toString() + extension;

            // 3. Dosyayı yükle
            try (InputStream inputStream = file.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .stream(inputStream, file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build()
                );
            }

            log.info("Dosya başarıyla yüklendi: {}", objectName);

            // 4. Doğrudan erişim URL'ini dön (Localhost MinIO için basit link şeması)
            // Prod S3'e geçtiğimizde AWS CloudFront veya S3 Public URL formatında güncelleyeceğiz
            return publicUrl + "/" + bucketName + "/" + objectName;

        } catch (Exception e) {
            log.error("Dosya yükleme hatası: {}", e.getMessage(), e);
            throw new RuntimeException("Dosya yüklenemedi: " + e.getMessage());
        }
    }

    /**
     * S3/MinIO üzerindeki dosyayı siler.
     * url: Dosyanın tam erişim adresi
     */
    public void deleteFile(String url) {
        if (url == null || url.isEmpty()) {
            return;
        }

        try {
            // URL içinden sadece dosya (object) ismini bulup çıkarmak için:
            // Örnek: http://localhost:9000/product-images/abc-uuid.jpg -> abc-uuid.jpg
            String keyword = bucketName + "/";
            if (url.contains(keyword)) {
                String objectName = url.substring(url.indexOf(keyword) + keyword.length());
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .build()
                );
                log.info("Dosya başarıyla silindi: {}", objectName);
            }
        } catch (Exception e) {
            log.error("Dosya silme hatası: {}", e.getMessage(), e);
        }
    }
}
