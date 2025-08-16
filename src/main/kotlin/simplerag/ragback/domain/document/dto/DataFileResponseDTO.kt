package simplerag.ragback.domain.document.dto

data class DataFileResponseList(
    val dataFileResponseList : List<DataFileResponse>,
)

data class DataFileResponse(
    val id: Long,
    val sha256: String,
)