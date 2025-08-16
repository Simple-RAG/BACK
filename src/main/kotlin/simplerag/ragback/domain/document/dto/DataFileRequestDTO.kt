package simplerag.ragback.domain.document.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class DataFileBulkCreateRequest(
    @field:Size(min = 1, message = "최소 하나 이상 업로드해야 합니다")
    @Valid
    val items: List<DataFileCreateItem>
)

data class DataFileCreateItem(
    @field:NotBlank(message = "title은 비어있을 수 없습니다")
    val title: String,

    @field:Size(max = 10, message = "태그는 최대 10개까지 가능합니다")
    val tags: List<String> = emptyList()
)
