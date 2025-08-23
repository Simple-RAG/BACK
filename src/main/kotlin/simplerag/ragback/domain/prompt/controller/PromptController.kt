package simplerag.ragback.domain.prompt.controller

import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import simplerag.ragback.domain.prompt.dto.PromptCreateRequest
import simplerag.ragback.domain.prompt.dto.PromptPreviewResponse
import simplerag.ragback.domain.prompt.service.PromptService
import simplerag.ragback.global.response.ApiResponse

@RestController
@RequestMapping("/api/v1/prompts")
class PromptController(
    private val promptService: PromptService
) {

    @PostMapping
    fun createPrompt(
        @RequestBody @Valid promptCreateRequest: PromptCreateRequest
    ): ApiResponse<PromptPreviewResponse> {
        val savedPrompt = promptService.createPrompt(promptCreateRequest)
        return ApiResponse.ok(savedPrompt)
    }

}