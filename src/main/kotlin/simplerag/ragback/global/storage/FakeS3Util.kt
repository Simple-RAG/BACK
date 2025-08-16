package simplerag.ragback.global.storage

import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import simplerag.ragback.global.util.S3Type
import simplerag.ragback.global.util.S3Util
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap

@Component
@Primary
@Profile("test")
class FakeS3Util : S3Util {

    private val store = ConcurrentHashMap<String, ByteArray>()

    override fun upload(file: MultipartFile, dir: S3Type): String {
        val clean = (file.originalFilename ?: "file")
            .substringAfterLast('/').substringAfterLast('\\').ifBlank { "file" }

        val hash = sha256(file.bytes).take(12)
        val key = "${dir.label}/${hash}_$clean"

        store[key] = file.bytes
        return urlFromKey(key)
    }

    override fun urlFromKey(key: String): String = "fake://$key"

    override fun deleteByUrl(url: String) { keyFromUrl(url)?.let { store.remove(it) } }

    override fun delete(key: String) { store.remove(key) }

    override fun keyFromUrl(url: String): String? =
        url.removePrefix("fake://").ifBlank { null }

    private fun sha256(bytes: ByteArray): String {
        val md = MessageDigest.getInstance("SHA-256")
        return md.digest(bytes).joinToString("") { "%02x".format(it) }
    }
}
