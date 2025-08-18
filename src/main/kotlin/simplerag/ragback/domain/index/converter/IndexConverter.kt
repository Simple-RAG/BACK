package simplerag.ragback.domain.index.converter

import simplerag.ragback.domain.index.dto.IndexCreateRequest
import simplerag.ragback.domain.index.dto.IndexDetailResponse
import simplerag.ragback.domain.index.dto.IndexPreviewResponse
import simplerag.ragback.domain.index.dto.IndexPreviewResponseList
import simplerag.ragback.domain.index.entity.Index


fun toIndex(createRequest: IndexCreateRequest): Index {
    return Index(
        snapshotName = createRequest.snapshotName.trim(),
        overlapSize = createRequest.overlapSize,
        chunkingSize = createRequest.chunkingSize,
        similarityMetric = createRequest.similarityMetric,
        topK = createRequest.topK,
        embeddingModel = createRequest.embeddingModel,
        reranker = createRequest.reranker
    )
}

fun toIndexPreviewResponseList(
    indexes: List<Index>
): IndexPreviewResponseList {
    val indexList = indexes.map { toIndexPreviewResponse(it) }
    return IndexPreviewResponseList(indexList)
}

fun toIndexPreviewResponse(index: Index): IndexPreviewResponse {
    return IndexPreviewResponse(
        indexId = index.id,
        snapshotName = index.snapshotName,
    )
}

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