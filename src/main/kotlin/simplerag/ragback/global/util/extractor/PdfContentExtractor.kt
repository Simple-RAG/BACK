package simplerag.ragback.global.util.extractor

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

@Component
class PdfContentExtractor : ContentExtractor {

    private val TYPE = "application/pdf"

    override fun supports(type: String): Boolean {
        return TYPE == type
    }

    override fun extract(file: MultipartFile): String {
        file.inputStream.use { input ->
            PDDocument.load(input).use { doc ->
                val stripper = PDFTextStripper()
                return stripper.getText(doc)
            }
        }
    }
}