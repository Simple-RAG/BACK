package simplerag.ragback.domain.index.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import org.hibernate.validator.constraints.Length
import simplerag.ragback.domain.index.entity.enums.EmbeddingModel
import simplerag.ragback.domain.index.entity.enums.SimilarityMetric

data class IndexCreateRequest(
    @field:Length(max = 255)
    @field:NotBlank
    val snapshotName: String,

    @field:Positive
    val chunkingSize: Int,

    @field:PositiveOrZero
    val overlapSize: Int,

    val similarityMetric: SimilarityMetric,

    @field:Positive
    val topK: Int,

    val embeddingModel: EmbeddingModel,

    val reranker: Boolean,
)

data class IndexUpdateRequest(
    @field:Length(max = 255)
    @field:NotBlank
    val snapshotName: String,

    @field:Positive
    val chunkingSize: Int,

    @field:PositiveOrZero
    val overlapSize: Int,

    val similarityMetric: SimilarityMetric,

    @field:Positive
    val topK: Int,

    val reranker: Boolean,
)