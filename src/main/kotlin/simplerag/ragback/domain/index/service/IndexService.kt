package simplerag.ragback.domain.index.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import simplerag.ragback.domain.index.converter.toIndex
import simplerag.ragback.domain.index.converter.toIndexDetailResponse
import simplerag.ragback.domain.index.converter.toIndexPreviewResponse
import simplerag.ragback.domain.index.converter.toIndexPreviewResponseList
import simplerag.ragback.domain.index.dto.*
import simplerag.ragback.domain.index.repository.IndexRepository
import simplerag.ragback.global.error.ErrorCode
import simplerag.ragback.global.error.IndexException

@Service
class IndexService(
    private val indexRepository: IndexRepository,
) {

    @Transactional
    fun createIndex(indexCreateRequest: IndexCreateRequest): IndexPreviewResponse {

        validateOverlap(indexCreateRequest.overlapSize, indexCreateRequest.chunkingSize)

        val createdIndex = indexRepository.save(toIndex(indexCreateRequest))
        return toIndexPreviewResponse(createdIndex)
    }

    @Transactional(readOnly = true)
    fun getIndexes(): IndexPreviewResponseList {
        val indexes = indexRepository.findAllByOrderByCreatedAtDesc()
        return toIndexPreviewResponseList(indexes)
    }

    @Transactional(readOnly = true)
    fun getIndex(indexId: Long): IndexDetailResponse {
        val index = indexRepository.findByIdOrNull(indexId) ?: throw IndexException(ErrorCode.NOT_FOUND)

        return toIndexDetailResponse(index)
    }

    @Transactional
    fun updateIndex(
        indexId: Long,
        indexUpdateRequest: IndexUpdateRequest
    ): IndexPreviewResponse {
        val index = indexRepository.findByIdOrNull(indexId) ?: throw IndexException(ErrorCode.NOT_FOUND)

        validateOverlap(indexUpdateRequest.overlapSize, indexUpdateRequest.chunkingSize)

        index.update(indexUpdateRequest)

        return toIndexPreviewResponse(index)
    }

    @Transactional
    fun deleteIndex(indexId: Long) {
        val index = indexRepository.findByIdOrNull(indexId) ?: throw IndexException(ErrorCode.NOT_FOUND)

        indexRepository.delete(index)
    }

    private fun validateOverlap(overlapSize: Int, chunkingSize: Int) {
        if (overlapSize >= chunkingSize) throw IndexException(ErrorCode.OVERLAP_OVERFLOW)
    }

}