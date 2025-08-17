package simplerag.ragback.domain.index.entity

import jakarta.persistence.*
import simplerag.ragback.domain.index.entity.enums.SimilarityMetric
import simplerag.ragback.global.entity.BaseEntity

@Entity
@Table(name = "indexes")
class Index(

    @Column(name = "snapshot_name", length = 255, nullable = false)
    val snapshotName: String,

    @Column(name = "chunking_size", nullable = false)
    val chunkingSize: Int,

    @Column(name = "overlap_size", nullable = false)
    val overlapSize: Int,

    @Column(name = "similarity_metric", nullable = false)
    val similarityMetric: SimilarityMetric,

    @Column(name = "top_k", nullable = false)
    val topK: Int,

    @Column(name = "embedding_model", nullable = false, length = 255)
    val embeddingModel: String,

    @Column(name = "reranker", nullable = false)
    val reranker: Boolean,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "indexes_id", nullable = false)
    val id: Long? = null,
): BaseEntity()