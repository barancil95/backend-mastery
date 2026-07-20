package com.baranproject.backendmastery.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO / S3 Bağlantı Konfigürasyonu
 * 
 * @Configuration: Bu sınıfın içerisinde Spring Bean tanımları (@Bean)
 *                 barındırdığını belirtir.
 *                 Spring ayağa kalkarken bu sınıftaki Bean metotlarını
 *                 çalıştırıp dönen nesneleri IoC Container'a kaydeder.
 */
@Configuration
public class MinioConfig {

    // application-dev.yml dosyasından değerleri enjekte ediyoruz
    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Value("${minio.region:eu-north-1}")
    private String region;

    /**
     * MinioClient nesnesini Spring Container'a bean olarak kaydediyoruz.
     * Bu nesne sayesinde dosya yükleme, silme gibi S3 API işlemlerini
     * gerçekleştireceğiz.
     */
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .region(region)
                .build();
    }
}
