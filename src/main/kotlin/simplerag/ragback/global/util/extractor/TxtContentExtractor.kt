package simplerag.ragback.global.util.extractor

import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

@Component
class TxtContentExtractor : ContentExtractor {
    override fun extract(file: MultipartFile): String {
        return file.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
    }
}