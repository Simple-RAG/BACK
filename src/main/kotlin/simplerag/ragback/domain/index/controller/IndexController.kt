package simplerag.ragback.domain.index.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import simplerag.ragback.domain.index.dto.*
import simplerag.ragback.domain.index.service.IndexService
import simplerag.ragback.global.response.ApiResponse

@RestController
@RequestMapping("/api/v1/indexes")
@Validated
class IndexController(
    private val indexService: IndexService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createIndex(
        @RequestBody indexCreateRequest: IndexCreateRequest
    ): ApiResponse<IndexPreviewResponse> {
        val createdIndex = indexService.createIndex(indexCreateRequest)
        return ApiResponse.ok(createdIndex)
    }

    @GetMapping
    fun getIndexes(): ApiResponse<IndexPreviewResponseList> {
        val indexPreviewResponseList = indexService.getIndexes()
        return ApiResponse.ok(indexPreviewResponseList)
    }

    @GetMapping("/{indexesId}")
    fun getIndex(
        @PathVariable indexesId: Long
    ): ApiResponse<IndexDetailResponse> {
        val indexDetailResponse = indexService.getIndex(indexesId)
        return ApiResponse.ok(indexDetailResponse)
    }

    @PutMapping("/{indexesId}")
    fun updateIndexes(
        @PathVariable indexesId: Long,
        @RequestBody @Valid indexUpdateRequest: IndexUpdateRequest,
    ): ApiResponse<IndexPreviewResponse> {
        val indexPreviewResponse = indexService.updateIndexes(indexesId, indexUpdateRequest)
        return ApiResponse.ok(indexPreviewResponse)
    }

    @DeleteMapping("/{indexesId}")
    fun deleteIndex(
        @PathVariable indexesId: Long
    ): ApiResponse<Unit> {
        indexService.deleteIndex(indexesId)
        return ApiResponse.ok(null, "인덱스가 삭제 되었습니다.")
    }


}