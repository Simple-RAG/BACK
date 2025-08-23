package simplerag.ragback.domain.prompt.dto

import simplerag.ragback.domain.prompt.entity.Prompt

data class PromptPreviewResponse(
    val id: Long,
) {
    companion object {
        fun from(
            prompt: Prompt
        ): PromptPreviewResponse {
            return PromptPreviewResponse(prompt.id)
        }
    }
}