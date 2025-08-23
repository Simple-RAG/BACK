package simplerag.ragback.domain.prompt.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import simplerag.ragback.domain.prompt.dto.PromptCreateRequest
import simplerag.ragback.domain.prompt.dto.PromptDetailResponse
import simplerag.ragback.domain.prompt.dto.PromptPreviewResponse
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

}