package simplerag.ragback.global.util

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.postgresql.util.PGobject

@Converter(autoApply = false)
class FloatArrayToPgVectorConverter : AttributeConverter<FloatArray, PGobject> {
    override fun convertToDatabaseColumn(attribute: FloatArray?): PGobject {
        requireNotNull(attribute) { "embedding must not be null" }
        require(attribute.isNotEmpty()) { "embedding must not be empty" }
        require(attribute.all { it.isFinite() }) { "NaN/Infinity not allowed" }

        val sb = StringBuilder(attribute.size * 8 + 2).append('[')
        attribute.forEachIndexed { i, v -> if (i > 0) sb.append(','); sb.append(v) }
        sb.append(']')

        return PGobject().apply {
            type = "vector"
            value = sb.toString()
        }
    }

    override fun convertToEntityAttribute(dbData: PGobject?): FloatArray {
        requireNotNull(dbData) { "db vector is null" }
        val body = dbData.value?.trim()?.removePrefix("[")?.removeSuffix("]") ?: error("empty vector")
        return body.split(',').map { it.trim().toFloat() }.toFloatArray()
    }
}
