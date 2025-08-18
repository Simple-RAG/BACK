package simplerag.ragback.domain.index.repository

import org.springframework.data.jpa.repository.JpaRepository
import simplerag.ragback.domain.index.entity.Index

interface IndexRepository: JpaRepository<Index, Long> {

    fun findAllByOrderByCreatedAt(): List<Index>

}