package simplerag.ragback.domain.prompt.service

import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import simplerag.ragback.domain.document.dto.DataFileDetailResponseList
import simplerag.ragback.domain.document.dto.TagDTO
import simplerag.ragback.domain.prompt.dto.*
import simplerag.ragback.domain.prompt.entity.Prompt
import simplerag.ragback.domain.prompt.repository.PromptRepository
import simplerag.ragback.global.error.ErrorCode
import simplerag.ragback.global.error.PromptException

@Service
@Transactional(readOnly = true)
class PromptService(
    private val promptRepository: PromptRepository
) {

    @Transactional
    fun createPrompt(
        promptCreateRequest: PromptCreateRequest
    ): PromptPreviewResponse {
        val prompt = Prompt.from(promptCreateRequest)
        val savedPrompt = promptRepository.save(prompt)
        return PromptPreviewResponse.from(savedPrompt)
    }

    fun getPrompt(promptId: Long): PromptDetailResponse {
        val prompt = promptRepository.findByIdOrNull(promptId) ?: throw  PromptException(ErrorCode.NOT_FOUND)
        return PromptDetailResponse.from(prompt)
    }

    fun getPrompts(
        cursor: Long,
        take: Int
    ): PromptPreviewResponseList {
        val prompts = promptRepository.findByIdGreaterThanOrderById(cursor, PageRequest.of(0, take))

        val nextCursor = prompts.content.lastOrNull()?.id

        return PromptPreviewResponseList.from(prompts.content, nextCursor, prompts.hasNext())
    }

    @Transactional
    fun updatePrompt(
        promptUpdateRequest: PromptUpdateRequest,
        promptId: Long
    ): PromptPreviewResponse {
        val prompt = promptRepository.findByIdOrNull(promptId) ?: throw  PromptException(ErrorCode.NOT_FOUND)
        prompt.update(promptUpdateRequest)
        return PromptPreviewResponse.from(prompt)
    }
}