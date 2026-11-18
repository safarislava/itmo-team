package ru.ifmo.se.api.infrastructure.storage

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Configuration
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import java.net.URI

@Configuration
class S3ClientConfiguration(
    @Value("\${storage.s3.endpoint}")
    private val endpoint: String,
    @Value("\${storage.s3.region}")
    private val region: String,
    @Value("\${storage.s3.access-key}")
    private val accessKey: String,
    @Value("\${storage.s3.secret-key}")
    private val secretKey: String,
    @Value("\${storage.s3.path-style-access-enabled}")
    private val pathStyleAccessEnabled: Boolean,
) {
    @Bean
    fun s3Client(): S3Client {
        val credentials = AwsBasicCredentials.create(accessKey, secretKey)
        val s3Config =
            S3Configuration
                .builder()
                .pathStyleAccessEnabled(pathStyleAccessEnabled)
                .build()

        return S3Client
            .builder()
            .endpointOverride(URI.create(endpoint))
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .serviceConfiguration(s3Config)
            .build()
    }

    @Bean
    fun s3Presigner(): S3Presigner {
        val credentials = AwsBasicCredentials.create(accessKey, secretKey)
        val s3Config =
            S3Configuration
                .builder()
                .pathStyleAccessEnabled(pathStyleAccessEnabled)
                .build()

        return S3Presigner
            .builder()
            .endpointOverride(URI.create(endpoint))
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .serviceConfiguration(s3Config)
            .build()
    }
}
