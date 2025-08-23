package simplerag.ragback.domain.prompt.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import simplerag.ragback.domain.prompt.dto.PromptCreateRequest
import simplerag.ragback.domain.prompt.dto.PromptPreviewResponse
import simplerag.ragback.domain.prompt.entity.Prompt
import simplerag.ragback.domain.prompt.repository.PromptRepository

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
}