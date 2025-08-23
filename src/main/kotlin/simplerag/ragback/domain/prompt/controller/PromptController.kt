package simplerag.ragback.domain.prompt.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import simplerag.ragback.domain.index.dto.IndexUpdateRequest
import simplerag.ragback.domain.prompt.dto.*
import simplerag.ragback.domain.prompt.service.PromptService
import simplerag.ragback.global.response.ApiResponse

@RestController
@RequestMapping("/api/v1/prompts")
class PromptController(
    private val promptService: PromptService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createPrompt(
        @RequestBody @Valid promptCreateRequest: PromptCreateRequest
    ): ApiResponse<PromptPreviewResponse> {
        val savedPrompt = promptService.createPrompt(promptCreateRequest)
        return ApiResponse.ok(savedPrompt)
    }

    @GetMapping("/{promptId}")
    fun getPrompt(
        @PathVariable promptId: Long,
    ): ApiResponse<PromptDetailResponse> {
        val promptDetail = promptService.getPrompt(promptId)
        return ApiResponse.ok(promptDetail)
    }

    @GetMapping
    fun getPrompts(
        @RequestParam cursor: Long,
        @RequestParam take: Int,
    ): ApiResponse<PromptPreviewResponseList> {
        val promptPreviewResponseList = promptService.getPrompts(cursor, take)
        return ApiResponse.ok(promptPreviewResponseList)
    }

    @PutMapping("/{promptId}")
    fun updatePrompt(
        @RequestBody @Valid promptUpdateRequest: PromptUpdateRequest,
        @PathVariable promptId: Long,
    ): ApiResponse<PromptPreviewResponse> {
        val updatedPrompt = promptService.updatePrompt(promptUpdateRequest, promptId)
        return ApiResponse.ok(updatedPrompt)
    }

    @DeleteMapping("/{promptId}")
    fun deletePrompt(
        @PathVariable promptId: Long,
    ): ApiResponse<Unit> {
        promptService.deletePrompt(promptId)
        return ApiResponse.ok(null, "프롬프트가 삭제되었습니다.")
    }

}