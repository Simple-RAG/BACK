package simplerag.ragback.domain.index.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import simplerag.ragback.domain.document.entity.DataFile
import simplerag.ragback.domain.document.repository.DataFileRepository
import simplerag.ragback.domain.index.dto.*
import simplerag.ragback.domain.index.embed.Embedder
import simplerag.ragback.domain.index.entity.ChunkEmbedding
import simplerag.ragback.domain.index.entity.Index
import simplerag.ragback.domain.index.repository.IndexRepository
import simplerag.ragback.global.error.CustomException
import simplerag.ragback.global.error.ErrorCode
import simplerag.ragback.global.error.IndexException
import simplerag.ragback.global.util.loader.ContentLoader
import simplerag.ragback.global.util.TextChunker

@Service
class IndexService(
    private val indexRepository: IndexRepository,
    private val embedder: Embedder,
    private val dataFileRepository: DataFileRepository,
    private val contentLoader: ContentLoader,
) {

    @Transactional
    fun createIndex(req: IndexCreateRequest): IndexPreviewResponse {
        validateOverlap(req.overlapSize, req.chunkingSize)

        val files: List<DataFile> = dataFileRepository.findAllById(req.dataFileId)
        if (files.size != req.dataFileId.size) {
            throw CustomException(ErrorCode.NOT_FOUND, "Some dataFileIds not found")
        }

        if (embedder.dim != req.embeddingModel.dim) {
            throw CustomException(ErrorCode.INVALID_INPUT, "Embedding dim mismatch: model=${req.embeddingModel.dim}, embedder=${embedder.dim}")
        }
        val index = indexRepository.save(Index.toIndex(req))

        for (file in files) {
            val url = file.fileUrl
            val content = contentLoader.load(url)
            if (content.isBlank()) continue

            val chunks = TextChunker.chunkByCharsSeq(content, req.chunkingSize, req.overlapSize)
            for (chunk in chunks) {
                val vec  = embedder.embed(chunk)
                val entity = ChunkEmbedding(
                    content = chunk,
                    embedding = vec,
                    embeddingDim = embedder.dim,
                    index = index
                )
                index.chunkEmbeddings.add(entity)
            }
        }

        return IndexPreviewResponse.toIndexPreviewResponse(index)
    }

    @Transactional(readOnly = true)
    fun getIndexes(): IndexPreviewResponseList {
        val indexes = indexRepository.findAllByOrderByCreatedAtDesc()
        return IndexPreviewResponseList.toIndexPreviewResponseList(indexes)
    }

    @Transactional(readOnly = true)
    fun getIndex(indexId: Long): IndexDetailResponse {
        val index = indexRepository.findByIdOrNull(indexId)
            ?: throw IndexException(ErrorCode.NOT_FOUND)

        return IndexDetailResponse.toIndexDetailResponse(index)
    }

    @Transactional
    fun updateIndex(
        indexId: Long,
        indexUpdateRequest: IndexUpdateRequest
    ): IndexPreviewResponse {
        val index = indexRepository.findByIdOrNull(indexId)
            ?: throw IndexException(ErrorCode.NOT_FOUND)

        validateOverlap(indexUpdateRequest.overlapSize, indexUpdateRequest.chunkingSize)

        index.update(indexUpdateRequest)

        return IndexPreviewResponse.toIndexPreviewResponse(index)
    }

    @Transactional
    fun deleteIndex(indexId: Long) {
        val index = indexRepository.findByIdOrNull(indexId)
            ?: throw IndexException(ErrorCode.NOT_FOUND)

        indexRepository.delete(index)
    }

    private fun validateOverlap(overlapSize: Int, chunkingSize: Int) {
        if (overlapSize >= chunkingSize) throw IndexException(ErrorCode.OVERLAP_OVERFLOW)
    }

}