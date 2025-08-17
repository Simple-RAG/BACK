package simplerag.ragback.domain.index.entity

import jakarta.persistence.*
import simplerag.ragback.global.entity.BaseEntity
import simplerag.ragback.global.util.FloatArrayToPgVectorStringConverter

@Entity
@Table(name = "chunk_embeddings")
class ChunkEmbedding(

    @Column(name = "content", nullable = false)
    @Lob
    val content: String,

    @Convert(converter = FloatArrayToPgVectorStringConverter::class)
    @Column(name = "embedding", nullable = false, columnDefinition = "vector(1536)") // 차원 수에 맞추세요
    var embedding: FloatArray,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "indexes_id", nullable = false)
    val index: Index,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chunk_embeddings_id")
    val id: Long? = null,
): BaseEntity()