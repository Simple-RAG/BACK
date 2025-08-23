package simplerag.ragback.domain.prompt.dto

import jakarta.validation.constraints.Size
import simplerag.ragback.domain.prompt.entity.enums.PreSet


data class PromptCreateRequest(
    @field:Size(max = 100)
    val name: String,
    val preSet: PreSet,
    @field:Size(max = 20000)
    val systemPrompt: String?,
)

data class PromptUpdateRequest(
    @field:Size(max = 100)
    val name: String,
    val preSet: PreSet,
    @field:Size(max = 20000)
    val systemPrompt: String?,
)