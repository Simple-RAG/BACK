package simplerag.ragback.domain.prompt.dto

import simplerag.ragback.domain.prompt.entity.enums.PreSet


data class PromptCreateRequest(
    val name: String,
    val preSet: PreSet,
    val systemPrompt: String,
)