package simplerag.ragback.domain.prompt.dto

import simplerag.ragback.domain.prompt.entity.Prompt
import simplerag.ragback.domain.prompt.entity.enums.PreSet

data class PromptPreviewResponseList(
    val promptPreviewResponseList: List<PromptPreviewResponse>,
    val cursor: Long?,
    val hasNext: Boolean
) {
    companion object {
        fun from(prompts: List<Prompt>, cursor: Long?, hasNext: Boolean): PromptPreviewResponseList =
            PromptPreviewResponseList(
                promptPreviewResponseList = prompts.map { prompt ->
                    PromptPreviewResponse.from(prompt)
                },
                cursor = cursor,
                hasNext = hasNext,
            )
    }
}

data class PromptPreviewResponse(
    val id: Long,
    val name: String,
) {
    companion object {
        fun from(
            prompt: Prompt
        ): PromptPreviewResponse {
            return PromptPreviewResponse(prompt.id, prompt.name)
        }
    }
}

data class PromptDetailResponse(
    val id: Long,
    val name: String,
    val preSet: PreSet,
    val systemPrompt: String,
) {
    companion object {
        fun from(
            prompt: Prompt
        ): PromptDetailResponse {
            return PromptDetailResponse(prompt.id, prompt.name, prompt.preSet, prompt.systemPrompt)
        }
    }
}