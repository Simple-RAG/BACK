package simplerag.ragback.global.util.extractor

import org.springframework.web.multipart.MultipartFile

interface ContentExtractor {
    fun supports(type: String): Boolean
    fun extract(file: MultipartFile): String
}