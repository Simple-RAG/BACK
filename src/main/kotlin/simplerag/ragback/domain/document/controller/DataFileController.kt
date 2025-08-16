package simplerag.ragback.domain.document.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import simplerag.ragback.domain.document.dto.DataFileBulkCreateRequest
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
        ],
        produces = [
            MediaType.APPLICATION_JSON_VALUE
        ]
    )
    @ResponseStatus(HttpStatus.CREATED)
    fun upload(
        @RequestPart("files") files: List<MultipartFile>,
        @Valid @RequestPart("request") req: DataFileBulkCreateRequest
    ): ApiResponse<DataFileResponseList> {
        val saved = dataFileService.upload(files, req)
        return ApiResponse.ok(saved, "업로드 완료")
    }

}
