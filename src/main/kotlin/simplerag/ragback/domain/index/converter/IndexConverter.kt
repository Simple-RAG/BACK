package simplerag.ragback.domain.index.converter

import simplerag.ragback.domain.index.dto.IndexCreateRequest
import simplerag.ragback.domain.index.dto.IndexPreviewResponse
import simplerag.ragback.domain.index.entity.Index


fun toIndex(createRequest: IndexCreateRequest): Index {
    return Index(
        snapshotName = createRequest.snapshotName,
        overlapSize = createRequest.overlapSize,
        chunkingSize = createRequest.chunkingSize,
        similarityMetric = createRequest.similarityMetric,
        topK = createRequest.topK,
        embeddingModel = createRequest.embeddingModel,
        reranker = createRequest.reranker
    )
}

fun toIndexPreviewResponse(index: Index): IndexPreviewResponse {
    return IndexPreviewResponse(
        indexesId = index.id
    )
}