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
        @RequestBody @Valid indexCreateRequest: IndexCreateRequest
    ): ApiResponse<IndexPreviewResponse> {
        val createdIndex = indexService.createIndex(indexCreateRequest)
        return ApiResponse.ok(createdIndex)
    }

    @GetMapping
    fun getIndexes(): ApiResponse<IndexPreviewResponseList> {
        val indexPreviewResponseList = indexService.getIndexes()
        return ApiResponse.ok(indexPreviewResponseList)
    }

    @GetMapping("/{indexId}")
    fun getIndex(
        @PathVariable indexId: Long
    ): ApiResponse<IndexDetailResponse> {
        val indexDetailResponse = indexService.getIndex(indexId)
        return ApiResponse.ok(indexDetailResponse)
    }

    @PutMapping("/{indexId}")
    fun updateIndexes(
        @PathVariable indexId: Long,
        @RequestBody @Valid indexUpdateRequest: IndexUpdateRequest,
    ): ApiResponse<IndexPreviewResponse> {
        val indexPreviewResponse = indexService.updateIndex(indexId, indexUpdateRequest)
        return ApiResponse.ok(indexPreviewResponse)
    }

    @DeleteMapping("/{indexId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteIndex(
        @PathVariable indexId: Long
    ): ApiResponse<Unit> {
        indexService.deleteIndex(indexId)
        return ApiResponse.ok(null, "인덱스가 삭제 되었습니다.")
    }


}