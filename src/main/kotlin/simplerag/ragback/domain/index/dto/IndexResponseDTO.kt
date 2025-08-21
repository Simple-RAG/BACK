package simplerag.ragback.domain.index.dto

import simplerag.ragback.domain.index.entity.Index
import simplerag.ragback.domain.index.entity.enums.EmbeddingModel
import simplerag.ragback.domain.index.entity.enums.SimilarityMetric

data class IndexPreviewResponseList(
    val indexPreviewResponseList: List<IndexPreviewResponse>,
) {
    companion object {
        fun toIndexPreviewResponseList(
            indexes: List<Index>
        ): IndexPreviewResponseList {
            val indexList = indexes.map { IndexPreviewResponse.toIndexPreviewResponse(it) }
            return IndexPreviewResponseList(indexList)
        }
    }
}

data class IndexPreviewResponse(
    var indexId: Long?,
    val snapshotName: String
) {
    companion object {
        fun toIndexPreviewResponse(index: Index): IndexPreviewResponse {
            return IndexPreviewResponse(
                indexId = index.id,
                snapshotName = index.snapshotName,
            )
        }
    }
}

data class IndexDetailResponse(
    var indexId: Long?,
    val snapshotName: String,
    val chunkingSize: Int,
    val overlapSize: Int,
    val similarityMetric: SimilarityMetric,
    val topK: Int,
    val embeddingModel: EmbeddingModel,
    val reranker: Boolean,
) {
    companion object {
        fun toIndexDetailResponse(index: Index): IndexDetailResponse {
            return IndexDetailResponse(
                indexId = index.id,
                chunkingSize = index.chunkingSize,
                overlapSize = index.overlapSize,
                similarityMetric = index.similarityMetric,
                topK = index.topK,
                embeddingModel = index.embeddingModel,
                reranker = index.reranker,
                snapshotName = index.snapshotName,
            )
        }
    }
}