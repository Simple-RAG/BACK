package simplerag.ragback.global.util

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = false)
class FloatArrayToPgVectorStringConverter : AttributeConverter<FloatArray, String> {
    override fun convertToDatabaseColumn(attribute: FloatArray?): String {
        requireNotNull(attribute) { "Embedding (FloatArray) must not be null" }
        require(attribute.isNotEmpty()) { "Embedding must not be empty; expected fixed dimension (e.g., 1536)" }
        require(attribute.all { !it.isNaN() && !it.isInfinite() }) {
            "Embedding must not contain NaN/Infinity"
        }
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
                .also { arr ->
                    require(arr.all { it.isFinite() }) {
                        "Embedding must not contain NaN/Infinity (db → entity)"
                    }
                }
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("Invalid vector literal for pgvector: '$dbData'", e)
        }
    }
}