package simplerag.ragback.domain.prompt.entity

import simplerag.ragback.domain.prompt.entity.enums.PreSet
import jakarta.persistence.*
import simplerag.ragback.domain.prompt.dto.PromptCreateRequest
import simplerag.ragback.domain.prompt.dto.PromptUpdateRequest
import simplerag.ragback.global.entity.BaseEntity
import simplerag.ragback.global.error.ErrorCode
import simplerag.ragback.global.error.PromptException

@Entity
@Table(name = "prompts")
class Prompt(

    @Column(name = "name", length = 100, nullable = false)
    var name: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "pre_set", nullable = false)
    var preSet: PreSet,

    @Column(name = "system_prompt", nullable = false)
    @Lob
    var systemPrompt: String,

    ) : BaseEntity() {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prompts_id")
    val id: Long = 0

    companion object {
        fun from(promptCreateRequest: PromptCreateRequest): Prompt =
            when (promptCreateRequest.preSet) {
                PreSet.CUSTOM -> Prompt(
                    promptCreateRequest.name,
                    promptCreateRequest.preSet,
                    promptCreateRequest.systemPrompt ?: ""
                )
                else -> {
                    if(!promptCreateRequest.systemPrompt.isNullOrBlank()) {
                        throw PromptException(ErrorCode.CUSTOM_SYSTEM_PROMPT)
                    }
                    Prompt(
                        promptCreateRequest.name,
                        promptCreateRequest.preSet,
                        promptCreateRequest.preSet.defaultSystemPrompt
                    )
                }
            }
    }

    fun update(promptUpdateRequest: PromptUpdateRequest) {
        when (promptUpdateRequest.preSet) {
            PreSet.CUSTOM -> {
                this.name = promptUpdateRequest.name
                this.systemPrompt = promptUpdateRequest.systemPrompt ?: ""
                this.preSet = promptUpdateRequest.preSet
            }
            else -> {
                if(!promptUpdateRequest.systemPrompt.isNullOrBlank()) {
                    throw PromptException(ErrorCode.CUSTOM_SYSTEM_PROMPT)
                }
                this.name = promptUpdateRequest.name
                this.systemPrompt = promptUpdateRequest.preSet.defaultSystemPrompt
                this.preSet = promptUpdateRequest.preSet
            }
        }
    }
}