package simplerag.ragback.global.util

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = false)
class FloatArrayJsonConverter : AttributeConverter<FloatArray, String> {
    override fun convertToDatabaseColumn(attribute: FloatArray?): String =
        attribute?.joinToString(prefix = "[", postfix = "]") { it.toString() } ?: "[]"

    override fun convertToEntityAttribute(dbData: String?): FloatArray {
        if (dbData.isNullOrBlank()) return floatArrayOf()
        // 매우 단순한 파서 (필요시 Jackson 등으로 교체)
        return dbData.trim().removePrefix("[").removeSuffix("]")
            .split(",")
            .mapNotNull { it.trim().toFloatOrNull() }
            .toFloatArray()
    }
}