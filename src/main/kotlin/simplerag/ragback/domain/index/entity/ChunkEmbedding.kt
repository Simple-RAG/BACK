package simplerag.ragback.domain.index.entity

import jakarta.persistence.*
import simplerag.ragback.global.entity.BaseEntity

@Entity
@Table(name = "chunk_embedding")
class ChunkEmbedding(

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    val content: String,

    @Column(name = "embedding", nullable = false)
    val embedding: FloatArray,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "indexes_id")
    val index: Index,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chunk_embedding_id")
    val id: Long? = null,
): BaseEntity()