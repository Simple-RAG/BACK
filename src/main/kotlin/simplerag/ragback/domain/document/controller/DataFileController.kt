package simplerag.ragback.domain.document.controller

import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import simplerag.ragback.domain.document.dto.DataFileBulkCreateRequest
import simplerag.ragback.domain.document.dto.DataFileResponseList
import simplerag.ragback.domain.document.service.DataFileService
import simplerag.ragback.global.response.ApiResponse

@RestController
@RequestMapping("/api/v1/data-files")
class DataFileController(
    private val service: DataFileService
) {

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun upload(
        @RequestPart("files") files: List<MultipartFile>,
        @Valid @RequestPart("request") req: DataFileBulkCreateRequest
    ): ApiResponse<DataFileResponseList> {
        val saved = service.upload(files, req)
        return ApiResponse.ok(saved, "업로드 완료")
    }

}
