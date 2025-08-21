package simplerag.ragback.domain.index.entity

import com.pgvector.PGvector
import jakarta.persistence.*
import simplerag.ragback.global.entity.BaseEntity

// 임베딩 크기를 서비스단에서 검증을 해줘야함
@Entity
@Table(name = "chunk_embeddings")
class ChunkEmbedding(

    @Column(name = "content", nullable = false)
    @Lob
    val content: String,

    @Column(name = "embedding", columnDefinition = "vector")
    var embedding: PGvector,

    @Column(name = "embedding_dim", nullable = false)
    val embeddingDim: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "indexes_id", nullable = false)
    val index: Index,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chunk_embeddings_id")
    val id: Long? = null,
) : BaseEntity()