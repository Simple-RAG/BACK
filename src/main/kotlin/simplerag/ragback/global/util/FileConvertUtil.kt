package simplerag.ragback.global.util

import org.springframework.web.multipart.MultipartFile
import java.io.BufferedInputStream
import java.security.DigestInputStream
import java.security.MessageDigest

data class FileMetrics(
    val sha256: String,
    val sizeByte: Long
)

fun sha256Hex(bytes: ByteArray): String =
    MessageDigest.getInstance("SHA-256")
        .digest(bytes)
        .joinToString("") { "%02x".format(it) }

fun MultipartFile.resolveContentType(): String {
    this.contentType?.let { return it }
    val ext = this.originalFilename?.substringAfterLast('.', "")?.lowercase()
    return when (ext) {
        "png" -> "image/png"
        "jpg", "jpeg" -> "image/jpeg"
        "pdf" -> "application/pdf"
        "txt" -> "text/plain"
        else -> "application/octet-stream"
    }
}

fun MultipartFile.computeMetricsStreaming(): FileMetrics {
    val digest = MessageDigest.getInstance("SHA-256")
    var totalBytes = 0L

    inputStream.use { input ->
        DigestInputStream(BufferedInputStream(input), digest).use { digestStream ->
            val buffer = ByteArray(8192) // 8KB buffer
            var bytesRead: Int

            while (digestStream.read(buffer).also { bytesRead = it } != -1) {
                totalBytes += bytesRead
            }
        }
    }

    val sha256 = digest.digest().joinToString("") { "%02x".format(it) }
    return FileMetrics(sha256, totalBytes)
}