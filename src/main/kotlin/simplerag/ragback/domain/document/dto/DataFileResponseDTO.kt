package simplerag.ragback.domain.document.dto

import simplerag.ragback.domain.document.entity.DataFile
import simplerag.ragback.domain.document.entity.DataFileTag
import simplerag.ragback.domain.document.entity.Tag
import java.time.LocalDateTime
import kotlin.math.round

data class DataFilePreviewResponseList(
    val dataFilePreviewResponseList: List<DataFilePreviewResponse>,
)

data class DataFilePreviewResponse(
    val id: Long,
    val sha256: String,
) {
    companion object {
        fun from(file: DataFile): DataFilePreviewResponse =
            DataFilePreviewResponse(
                id = requireNotNull(file.id) { "DataFile.id is null" },
                sha256 = file.sha256,
            )
    }
}

data class DataFileDetailResponseList(
    val dataFileDetailResponseList: List<DataFileDetailResponse>,
    val cursor: Long?,
    val hasNext: Boolean,
) {
    companion object {
        fun from(files: List<DataFile>, tagsByFileId: Map<Long, List<TagDTO>>, cursor: Long?, hasNext: Boolean): DataFileDetailResponseList =
            DataFileDetailResponseList(
                dataFileDetailResponseList = files.map { file ->
                    val tags = file.id?.let { tagsByFileId[it] } ?: emptyList()
                    DataFileDetailResponse.from(file, tags)
                },
                cursor = cursor,
                hasNext = hasNext,
            )
    }
}

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
        fun from(file: DataFile, tags: List<TagDTO>): DataFileDetailResponse =
            DataFileDetailResponse(
                id = requireNotNull(file.id) { "DataFile.id is null" },
                title = file.title,
                type = file.type,
                lastModified = file.updatedAt,
                sizeMB = file.sizeBytes.toMegaBytes(2),
                tags = tags,
                sha256 = file.sha256,
            )
    }
}

data class TagDTO(
    val id: Long?,
    val name: String,
) {
    companion object {
        fun from(tag: Tag): TagDTO = TagDTO(tag.id, tag.name)

        fun from(dataFileTags: List<DataFileTag>): List<TagDTO> =
            dataFileTags.map { from(it.tag) }
    }
}

private fun Long.toMegaBytes(precision: Int = 2): Double {
    val mb = this / (1024.0 * 1024.0)
    if (precision <= 0) return mb
    val scale = Math.pow(10.0, precision.toDouble())
    return round(mb * scale) / scale
}
