package simplerag.ragback.global.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Configuration
import software.amazon.awssdk.services.s3.presigner.S3Presigner

@Configuration
@Profile("!test")
class S3Config(

    @Value("\${cloud.aws.region.static}")
    private val region: String,

    @Value("\${cloud.aws.s3.bucket}")
    val bucket: String,

    @Value("\${cloud.aws.credentials.access-key:}")
    private val accessKey: String,

    @Value("\${cloud.aws.credentials.secret-key:}")
    private val secretKey: String,
) {

    @Bean
    fun s3Client(): S3Client {
        val regionObj = Region.of(region)

        val creds: AwsCredentialsProvider =
            if (accessKey.isNotBlank() && secretKey.isNotBlank())
                StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey))
            else
                DefaultCredentialsProvider.create()

        val builder = S3Client.builder()
            .region(regionObj)
            .credentialsProvider(creds)
            .serviceConfiguration(
                S3Configuration.builder()
                    .checksumValidationEnabled(true)
                    .build()
            )

        return builder.build()
    }

    @Bean
    fun s3Presigner(): S3Presigner {
        val regionObj = Region.of(region)

        val creds: AwsCredentialsProvider =
            if (accessKey.isNotBlank() && secretKey.isNotBlank())
                StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey))
            else
                DefaultCredentialsProvider.create()

        val builder = S3Presigner.builder()
            .region(regionObj)
            .credentialsProvider(creds)

        return builder.build()
    }
}