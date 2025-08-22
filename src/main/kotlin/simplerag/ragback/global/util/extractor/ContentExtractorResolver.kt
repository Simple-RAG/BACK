package simplerag.ragback.global.util.extractor

import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

@Component
class ContentExtractorResolver(
    private val extractors: List<ContentExtractor>
) {

    fun extractContent(file: MultipartFile, type: String): String {
        val extractor = extractors.find { it.supports(type) }
            ?: throw IllegalArgumentException("지원하지 않는 파일 타입입니다: $type")
        return extractor.extract(file)
    }
}