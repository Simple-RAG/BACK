package simplerag.ragback.global.util

import org.springframework.web.multipart.MultipartFile
import java.math.BigDecimal
import java.math.RoundingMode
import java.security.MessageDigest
import kotlin.math.round

fun byteToMegaByte(bytes: ByteArray): Double =
    BigDecimal(bytes.size.toDouble() / (1024.0 * 1024.0))
        .setScale(3, RoundingMode.HALF_UP)
        .toDouble()

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