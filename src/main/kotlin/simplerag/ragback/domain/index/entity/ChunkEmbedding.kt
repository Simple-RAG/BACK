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
    @Column(name = "embedding", nullable = false)
    private var _embedding: FloatArray,

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
        verifyDimAndValues(index.embeddingModel.dim, newVec)
        _embedding = newVec.copyOf()
    }

    @PrePersist
    @PreUpdate
    fun verifyBeforeSave() {
        verifyDimAndValues(index.embeddingModel.dim, _embedding)
    }

    private fun verifyDimAndValues(expected: Int, vec: FloatArray) {
        require(vec.isNotEmpty()) { "Embedding must not be empty" }
        require(vec.size == expected) {
            "Embedding dimension must be $expected but was ${vec.size}"
        }
        require(vec.all { it.isFinite() }) {
            "Embedding must not contain NaN/Infinity"
        }
    }

}