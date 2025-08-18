package simplerag.ragback.domain.index.dto

import simplerag.ragback.domain.index.entity.enums.EmbeddingModel
import simplerag.ragback.domain.index.entity.enums.SimilarityMetric

data class IndexPreviewResponse(
    var indexesId: Long?,
)

data class IndexDetailResponseList(
   val indexDetailResponse: List<IndexDetailResponse>,
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