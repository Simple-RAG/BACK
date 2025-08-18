package simplerag.ragback.domain.index.entity

import jakarta.persistence.*
import jakarta.validation.constraints.Min
import simplerag.ragback.domain.index.dto.IndexUpdateRequest
import simplerag.ragback.domain.index.entity.enums.EmbeddingModel
import simplerag.ragback.domain.index.entity.enums.SimilarityMetric
import simplerag.ragback.global.entity.BaseEntity

@Entity
@Table(name = "indexes")
class Index(

    @Column(name = "snapshot_name", length = 255, nullable = false)
    var snapshotName: String,

    @Column(name = "chunking_size", nullable = false)
    @Min(1)
    var chunkingSize: Int,

    @Column(name = "overlap_size", nullable = false)
    @Min(0)
    var overlapSize: Int,

    @Column(name = "similarity_metric", nullable = false)
    @Enumerated(EnumType.STRING)
    var similarityMetric: SimilarityMetric,

    @Column(name = "top_k", nullable = false)
    @Min(1)
    var topK: Int,

    @Column(name = "embedding_model", nullable = false, length = 255)
    @Enumerated(EnumType.STRING)
    val embeddingModel: EmbeddingModel,

    @Column(name = "reranker", nullable = false)
    var reranker: Boolean,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "index")
    val chunkingEmbeddingList: MutableList<ChunkEmbedding> = mutableListOf(),

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "indexes_id")
    var id: Long? = null,
) : BaseEntity() {

    fun update(req: IndexUpdateRequest) {
        snapshotName = req.snapshotName.trim()
        chunkingSize = req.chunkingSize
        overlapSize = req.overlapSize
        similarityMetric = req.similarityMetric
        topK = req.topK
        reranker = req.reranker
    }

}