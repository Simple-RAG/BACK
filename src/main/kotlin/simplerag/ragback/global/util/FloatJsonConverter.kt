package simplerag.ragback.global.util

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = false)
class FloatArrayToPgVectorStringConverter : AttributeConverter<FloatArray, String> {
    override fun convertToDatabaseColumn(attribute: FloatArray?): String {
        requireNotNull(attribute) { "Embedding (FloatArray) must not be null" }
        require(attribute.isNotEmpty()) { "Embedding must not be empty; expected fixed dimension (e.g., 1536)" }
        return attribute.joinToString(prefix = "[", postfix = "]", separator = ",") { it.toString() }
    }

    override fun convertToEntityAttribute(dbData: String?): FloatArray {
        if (dbData.isNullOrBlank()) return floatArrayOf()
        val body = dbData.trim().removePrefix("[").removeSuffix("]").trim()
        if (body.isBlank()) return floatArrayOf()
        return try {
            body.split(',')
                .map { it.trim().toFloat() }
                .toFloatArray()
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("Invalid vector literal for pgvector: '$dbData'", e)
        }
    }
}