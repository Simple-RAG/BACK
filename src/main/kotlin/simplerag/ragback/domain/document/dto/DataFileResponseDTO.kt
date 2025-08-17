package simplerag.ragback.domain.document.dto

import simplerag.ragback.domain.document.entity.DataFile
import java.time.LocalDateTime

data class DataFileResponseList(
    val dataFilePreviewResponseList: List<DataFilePreviewResponse>,
)

data class DataFilePreviewResponse(
    val id: Long,
    val sha256: String,
)

data class DataFileDetailResponseList(
    val dataFileDetailResponseList: List<DataFileDetailResponse>,
    val cursor: Long?,
    val hasNext: Boolean,
)

data class DataFileDetailResponse(
    var id: Long?,
    val title: String,
    val type: String,
    val lastModified: LocalDateTime,
    val sizeMB: Double,
    val tags: List<TagDTO>,
    val sha256: String,
) {
    companion object {
        fun of(dataFile: DataFile, tags: List<TagDTO>): DataFileDetailResponse {
            return DataFileDetailResponse(
                dataFile.id,
                dataFile.title,
                dataFile.type,
                dataFile.updatedAt,
                dataFile.sizeBytes / (1024.0 * 1024.0),
                tags,
                dataFile.sha256,
            )
        }
    }
}

data class TagDTO(
    val id: Long?,
    val name: String,
)
