package simplerag.ragback.domain.document.controller

import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import simplerag.ragback.domain.document.dto.DataFileBulkCreateRequest
import simplerag.ragback.domain.document.dto.DataFileDetailResponseList
import simplerag.ragback.domain.document.dto.DataFileResponseList
import simplerag.ragback.domain.document.service.DataFileService
import simplerag.ragback.global.response.ApiResponse

@RestController
@RequestMapping("/api/v1/data-files")
@Validated
class DataFileController(
    private val dataFileService: DataFileService
) {

    @PostMapping(
        consumes = [
            MediaType.MULTIPART_FORM_DATA_VALUE,
        ]
    )
    @ResponseStatus(HttpStatus.CREATED)
    fun upload(
        @RequestPart("files")
        @Size(min = 1, message = "최소 하나 이상 업로드해야 합니다")
        files: List<MultipartFile>,

        @Parameter(content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE)])
        @RequestPart("request")
        @Valid
        req: DataFileBulkCreateRequest
    ): ApiResponse<DataFileResponseList> {
        val saved = dataFileService.upload(files, req)
        return ApiResponse.ok(saved, "업로드 완료")
    }

    @GetMapping
    fun getDataFiles(
        @RequestParam(name = "cursor") cursor: Long,
        @RequestParam(name = "take") take: Int,
    ): ApiResponse<DataFileDetailResponseList> {
        val data = dataFileService.getDataFiles(cursor, take)
        return ApiResponse.ok(data)
    }

    @DeleteMapping("/{dataFilesId}")
    fun deleteFile(
        @PathVariable dataFilesId: Long,
    ): ApiResponse<Unit> {
        dataFileService.deleteFile(dataFilesId)
        return ApiResponse.ok(null, "데이터 삭제 완료")
    }

}
