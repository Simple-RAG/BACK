package simplerag.ragback.global.util

import org.springframework.web.multipart.MultipartFile

interface S3Util {
    fun upload(file: MultipartFile, dir: S3Type): String
    fun urlFromKey(key: String): String
    fun deleteByUrl(url: String)
    fun delete(key: String)
    fun keyFromUrl(url: String): String?
}