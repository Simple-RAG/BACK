package simplerag.ragback.global.util.converter

import org.springframework.web.multipart.MultipartFile
import simplerag.ragback.global.error.CustomException
import simplerag.ragback.global.error.ErrorCode
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
    if (!this.contentType.isNullOrBlank()) return this.contentType!!
    val ext = this.originalFilename?.substringAfterLast('.', "")?.lowercase()
    return when (ext) {
        "pdf" -> "application/pdf"
        "txt" -> "text/plain"
        "csv" -> "text/csv"
        "md" -> "text/markdown"
        "json" -> "application/json"
        "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        else -> throw CustomException(ErrorCode.INVALID_FILE_TYPE)
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