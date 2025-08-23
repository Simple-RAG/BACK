package simplerag.ragback.domain.prompt.repository

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import simplerag.ragback.domain.prompt.entity.Prompt

interface PromptRepository: JpaRepository<Prompt, Long> {
    fun findByIdGreaterThanOrderById(cursor: Long, pageable: Pageable): Slice<Prompt>
}