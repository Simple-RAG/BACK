package simplerag.ragback.domain.prompt.repository

import org.springframework.data.jpa.repository.JpaRepository
import simplerag.ragback.domain.prompt.entity.Prompt

interface PromptRepository: JpaRepository<Prompt, Long>