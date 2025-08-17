package simplerag.ragback.domain.index.entity

import jakarta.persistence.*
import simplerag.ragback.global.entity.BaseEntity
import simplerag.ragback.global.util.FloatArrayToPgVectorStringConverter

// 임베딩 크기를 서비스단에서 검증을 해줘야함
@Entity
@Table(name = "chunk_embeddings")
class ChunkEmbedding(

    @Column(name = "content", nullable = false)
    @Lob
    val content: String,

    @Convert(converter = FloatArrayToPgVectorStringConverter::class)
    @Column(name = "embedding", nullable = false)
    private var _embedding: FloatArray,

    @Column(name = "embedding_dim", nullable = false)
    val embeddingDim: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "indexes_id", nullable = false)
    val index: Index,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chunk_embeddings_id")
    val id: Long? = null,
): BaseEntity() {

    @get:Transient
    val embedding: FloatArray get() = _embedding.copyOf()

    fun updateEmbedding(newVec: FloatArray) {
        require(newVec.size == embeddingDim) {
            "Embedding dimension mismatch: expected=$embeddingDim, got=${newVec.size}"
        }
        _embedding = newVec.copyOf()
    }

}