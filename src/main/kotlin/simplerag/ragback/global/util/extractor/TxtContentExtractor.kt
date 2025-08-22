package simplerag.ragback.global.util.extractor

import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

@Component
class TxtContentExtractor : ContentExtractor {

    private val TYPE = listOf(
        "text/plain",
        "text/csv",
        "text/markdown",
        "application/json",
        "text/html",
    )

    override fun supports(type: String): Boolean {
        return type in TYPE
    }

    override fun extract(file: MultipartFile): String {
        return file.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
    }
}