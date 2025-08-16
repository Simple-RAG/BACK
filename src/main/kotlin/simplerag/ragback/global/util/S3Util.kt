package simplerag.ragback.global.util

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import simplerag.ragback.global.config.S3Config
import simplerag.ragback.global.error.ErrorCode
import simplerag.ragback.global.error.S3Exception
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.ObjectCannedACL
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import java.net.URI
import java.util.UUID

@Component
class S3Util(
    private val s3: S3Client,
    private val presigner: S3Presigner,
    private val s3Config: S3Config,
    @Value("\${cloud.aws.region.static}") private val region: String
) {
    private val bucket get() = s3Config.bucket

    fun upload(file: MultipartFile, dir: S3Type): String {
        if (file.isEmpty) throw S3Exception(ErrorCode.S3_EMPTY_FILE)

        val key = buildKey(dir.label, file.originalFilename)

        try {
            file.inputStream.use { input ->
                val putReq = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.contentType ?: "application/octet-stream")
                    .build()

                val body = RequestBody.fromInputStream(input, file.size)
                s3.putObject(putReq, body)
            }

            return urlFromKey(key)
        } catch (e: software.amazon.awssdk.services.s3.model.S3Exception) {
            throw S3Exception(ErrorCode.S3_UPLOAD_FAIL)
        } catch (e: Exception) {
            throw S3Exception(ErrorCode.S3_UPLOAD_FAIL)
        }
    }

    fun urlFromKey(key: String): String =
        s3.utilities()
            .getUrl { it.bucket(bucket).key(key) }
            .toExternalForm()

    fun deleteByUrl(url: String) {
        val key = keyFromUrl(url) ?: throw S3Exception(ErrorCode.S3_INVALID_URL)
        delete(key)
    }

    fun delete(key: String) {
        try {
            val req = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build()
            s3.deleteObject(req)
        } catch (e: software.amazon.awssdk.services.s3.model.S3Exception) {
            // NoSuchKey 등
            throw S3Exception(ErrorCode.S3_OBJECT_NOT_FOUND)
        } catch (e: Exception) {
            throw S3Exception(ErrorCode.S3_DELETE_FAIL)
        }
    }

    private fun buildKey(dir: String, originalFilename: String?): String {
        val cleanName = (originalFilename ?: "file")
            .substringAfterLast('/')
            .substringAfterLast('\\')
            .ifBlank { "file" }

        val prefix = dir.trim('/')

        return "$prefix/${UUID.randomUUID()}_$cleanName"
    }

    fun keyFromUrl(url: String): String? {
        val path = try {
            URI(url).path // e.g. "/market/menu/uuid_name.jpg"
        } catch (_: Exception) {
            return null
        }
        return path.removePrefix("/").ifBlank { null }
    }
}
