package simplerag.ragback.domain.index.repository

import org.springframework.data.jpa.repository.JpaRepository
import simplerag.ragback.domain.index.entity.ChunkEmbedding
import simplerag.ragback.domain.index.entity.Index

interface ChunkingEmbeddingRepository: JpaRepository<ChunkEmbedding, Long> {

    fun deleteAllByIndex(index: Index)

}