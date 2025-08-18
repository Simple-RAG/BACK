package simplerag.ragback.domain.index.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import simplerag.ragback.domain.index.converter.*
import simplerag.ragback.domain.index.dto.*
import simplerag.ragback.domain.index.repository.IndexRepository
import simplerag.ragback.global.error.ErrorCode
import simplerag.ragback.global.error.IndexException

@Service
class IndexService(
    private val indexRepository: IndexRepository
) {

    @Transactional
    fun createIndex(indexCreateRequest: IndexCreateRequest): IndexPreviewResponse {

        if (indexCreateRequest.overlapSize > indexCreateRequest.chunkingSize) {
            throw IndexException(ErrorCode.OVERLAP_OVERFLOW)
        }

        val createdIndex = indexRepository.save(toIndex(indexCreateRequest))
        return toIndexPreviewResponse(createdIndex)
    }

    @Transactional(readOnly = true)
    fun getIndexes(): IndexPreviewResponseList {
        val indexes = indexRepository.findAllByOrderByCreatedAt()
        return toIndexPreviewResponseList(indexes)
    }

    @Transactional(readOnly = true)
    fun getIndex(indexesId: Long): IndexDetailResponse? {
        val index = indexRepository.findIndexById(indexesId) ?: throw IndexException(ErrorCode.NOT_FOUND)

        return toIndexDetailResponse(index)
    }

    @Transactional
    fun updateIndexes(
        indexesId: Long,
        indexUpdateRequest: IndexUpdateRequest
    ): IndexPreviewResponse {
        val index = indexRepository.findIndexById(indexesId) ?: throw IndexException(ErrorCode.NOT_FOUND)

        if (indexUpdateRequest.overlapSize > indexUpdateRequest.chunkingSize) {
            throw IndexException(ErrorCode.OVERLAP_OVERFLOW)
        }

        index.update(indexUpdateRequest)

        return toIndexPreviewResponse(index)
    }

    @Transactional
    fun deleteIndex(indexesId: Long) {
        val index = indexRepository.findIndexById(indexesId) ?: throw IndexException(ErrorCode.NOT_FOUND)

        indexRepository.delete(index)
    }

}