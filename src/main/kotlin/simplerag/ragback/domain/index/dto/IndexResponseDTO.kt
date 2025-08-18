package simplerag.ragback.domain.index.dto

import simplerag.ragback.domain.index.entity.enums.EmbeddingModel
import simplerag.ragback.domain.index.entity.enums.SimilarityMetric

data class IndexPreviewResponseList(
    val indexDetailResponse: List<IndexPreviewResponse>,
)

data class IndexPreviewResponse(
    var indexesId: Long?,
    val snapshotName: String
)

data class IndexDetailResponse(
    var indexesId: Long?,
    val snapshotName: String,
    val chunkingSize: Int,
    val overlapSize: Int,
    val similarityMetric: SimilarityMetric,
    val topK: Int,
    val embeddingModel: EmbeddingModel,
    val reranker: Boolean,
)