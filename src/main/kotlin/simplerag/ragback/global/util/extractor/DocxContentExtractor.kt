package simplerag.ragback.global.util.extractor

import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

@Component
class DocxContentExtractor : ContentExtractor {

    private val TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"

    override fun supports(type: String): Boolean {
        return TYPE == type
    }

    override fun extract(file: MultipartFile): String {
        file.inputStream.use { input ->
            XWPFDocument(input).use { doc ->
                return doc.paragraphs.joinToString("\n") { it.text }
            }
        }
    }
}