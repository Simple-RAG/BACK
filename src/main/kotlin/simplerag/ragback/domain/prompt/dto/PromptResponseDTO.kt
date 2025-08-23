package simplerag.ragback.domain.prompt.dto

import jakarta.persistence.Column
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Lob
import simplerag.ragback.domain.prompt.entity.Prompt
import simplerag.ragback.domain.prompt.entity.enums.PreSet

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