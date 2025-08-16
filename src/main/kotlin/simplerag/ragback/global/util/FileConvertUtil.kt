package simplerag.ragback.global.util

import org.springframework.web.multipart.MultipartFile
import java.security.MessageDigest

fun byteToLong(bytes: ByteArray): Long =
    bytes.size.toLong()

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