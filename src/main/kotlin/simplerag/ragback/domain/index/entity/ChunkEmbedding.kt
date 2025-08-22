package simplerag.ragback.domain.index.entity

import jakarta.persistence.*
import simplerag.ragback.global.entity.BaseEntity

// 임베딩 크기를 서비스단에서 검증을 해줘야함
@Entity
@Table(name = "chunk_embeddings")
class ChunkEmbedding(

    @Column(name = "content", nullable = false, columnDefinition = "text")
    val content: String,

    @Column(name = "embedding", columnDefinition = "vector(1536)", nullable = false)
    var embedding: FloatArray,

    @Column(name = "embedding_dim", nullable = false)
    val embeddingDim: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "indexes_id", nullable = false)
    val index: Index,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chunk_embeddings_id")
    val id: Long? = null,
) : BaseEntity()