package simplerag.ragback.domain.index.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import simplerag.ragback.domain.index.dto.IndexCreateRequest
import simplerag.ragback.domain.index.dto.IndexDetailResponseList
import simplerag.ragback.domain.index.dto.IndexPreviewResponse
import simplerag.ragback.domain.index.dto.IndexUpdateRequest
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
    fun getIndexes(): ApiResponse<IndexDetailResponseList> {
        val indexDetailResponseList = indexService.getIndexes()
        return ApiResponse.ok(indexDetailResponseList)
    }

    @PutMapping("/{indexesId}")
    fun updateIndexes(
        @PathVariable indexesId: Long,
        @RequestBody @Valid indexUpdateRequest: IndexUpdateRequest,
    ): ApiResponse<IndexPreviewResponse> {
        val indexPreviewResponse = indexService.updateIndexes(indexesId, indexUpdateRequest)
        return ApiResponse.ok(indexPreviewResponse)
    }

}