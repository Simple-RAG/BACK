package simplerag.ragback.domain.index.repository

import org.springframework.data.jpa.repository.JpaRepository
import simplerag.ragback.domain.index.entity.ChunkEmbedding

interface ChunkEmbeddingRepository : JpaRepository<ChunkEmbedding, Long>