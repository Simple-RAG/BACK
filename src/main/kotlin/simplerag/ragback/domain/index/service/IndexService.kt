package simplerag.ragback.domain.index.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import simplerag.ragback.domain.index.converter.toIndex
import simplerag.ragback.domain.index.converter.toIndexPreviewResponse
import simplerag.ragback.domain.index.dto.IndexCreateRequest
import simplerag.ragback.domain.index.dto.IndexPreviewResponse
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

}