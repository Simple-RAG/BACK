package simplerag.ragback.global.util

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = false)
class FloatArrayToPgVectorStringConverter : AttributeConverter<FloatArray, String> {
    override fun convertToDatabaseColumn(attribute: FloatArray?): String {
        if (attribute == null) return "[]"
        return buildString {
            append('[')
            attribute.forEachIndexed { i, v ->
                if (i > 0) append(',')
                append(v.toString())
            }
            append(']')
        }
    }
    override fun convertToEntityAttribute(dbData: String?): FloatArray {
        if (dbData.isNullOrBlank()) return floatArrayOf()
        val body = dbData.trim().removePrefix("[").removeSuffix("]")
        if (body.isBlank()) return floatArrayOf()
        return body.split(',').map { it.trim().toFloat() }.toFloatArray()
    }
}