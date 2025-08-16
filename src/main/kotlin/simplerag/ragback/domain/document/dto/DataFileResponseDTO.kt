package simplerag.ragback.domain.document.dto

data class DataFileResponseList(
    val dataFilePreviewResponseList : List<DataFilePreviewResponse>,
)

data class DataFilePreviewResponse(
    val id: Long,
    val sha256: String,
)