package simplerag.ragback.global.util.extractor

import org.springframework.web.multipart.MultipartFile

interface ContentExtractor {
    fun extract(file: MultipartFile): String
}