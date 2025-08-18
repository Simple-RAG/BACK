package simplerag.ragback.domain.index.dto

import org.hibernate.validator.constraints.Length
import simplerag.ragback.domain.index.entity.enums.EmbeddingModel
import simplerag.ragback.domain.index.entity.enums.SimilarityMetric

data class IndexCreateRequest(
    @Length(max = 255)
    val snapshotName: String,

    @Length(min = 1)
    val chunkingSize: Int,

    @Length(min = 0)
    val overlapSize: Int,

    val similarityMetric: SimilarityMetric,

    @Length(min = 1)
    val topK: Int,

    val embeddingModel: EmbeddingModel,

    val reranker: Boolean,
)