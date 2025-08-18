package simplerag.ragback.domain.index.service

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import simplerag.ragback.domain.index.dto.IndexCreateRequest
import simplerag.ragback.domain.index.entity.Index
import simplerag.ragback.domain.index.entity.enums.EmbeddingModel
import simplerag.ragback.domain.index.entity.enums.SimilarityMetric
import simplerag.ragback.domain.index.repository.IndexRepository
import simplerag.ragback.global.error.IndexException
import org.assertj.core.api.Assertions.assertThat
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ActiveProfiles("test")
class IndexServiceTest (
    @Autowired private val indexRepository: IndexRepository,
    @Autowired private val indexService: IndexService,
) {

    @AfterEach
    fun cleanUp() {
        indexRepository.deleteAll()
    }

    @Test
    @DisplayName("인덱스 생성이 정상 작동한다")
    fun createIndexTest() {
        // given
        val indexCreateRequest = IndexCreateRequest("test", 1, 0, SimilarityMetric.COSINE, 1, EmbeddingModel.TEXT_EMBEDDING_3_LARGE, true)

        // when
        val createIndexResponse = indexService.createIndex(indexCreateRequest);

        // then
        val indices = indexRepository.findAll()
        val index = indices[0]
        assertEquals(index.id, createIndexResponse.indexId)
        assertEquals(index.snapshotName, createIndexResponse.snapshotName)
    }

    @Test
    @DisplayName("인덱스 생성 시 overlap 크기가 chunking 크기를 넘어가면 에러가 터진다")
    fun createIndexTestWithOverlapSize() {
        // given
        val indexCreateRequest = IndexCreateRequest("test", 1, 1, SimilarityMetric.COSINE, 1, EmbeddingModel.TEXT_EMBEDDING_3_LARGE, true)

        // when * then
        val message = assertThrows<IndexException> {
            indexService.createIndex(indexCreateRequest)
        }.message

        assertEquals(message, "overlap 크기는 chunking 크기를 넘을 수 없습니다.")
    }

    @Test
    @DisplayName("인덱스 리스트 조회가 된다")
    fun getIndexesTest() {
        // given
        indexRepository.saveAll(
            listOf(
                Index(
                "test",
                1,
                0,
                SimilarityMetric.COSINE,
                1,
                EmbeddingModel.TEXT_EMBEDDING_3_LARGE,
                true
                ),
                Index(
                    "test2",
                    1,
                    0,
                    SimilarityMetric.COSINE,
                    1,
                    EmbeddingModel.TEXT_EMBEDDING_3_LARGE,
                    true
                )
            )
        )

        // when
        val indexes = indexService.getIndexes()

        // then
        assertThat(indexes.indexDetailResponse.size).isEqualTo(2)
    }

    @Test
    @DisplayName("인덱스 상세 조회가 된다")
    fun getIndexTest() {
        // given
        val savedIndex = indexRepository.save(
            Index(
                "test",
                1,
                0,
                SimilarityMetric.COSINE,
                1,
                EmbeddingModel.TEXT_EMBEDDING_3_LARGE,
                true
            )
        )

        // when
        val index = indexService.getIndex(savedIndex.id!!)

        // then
        assertThat(index.indexId).isEqualTo(savedIndex.id)
        assertThat(index.snapshotName).isEqualTo(savedIndex.snapshotName)
        assertThat(index.chunkingSize).isEqualTo(savedIndex.chunkingSize)
        assertThat(index.overlapSize).isEqualTo(savedIndex.overlapSize)
        assertThat(index.topK).isEqualTo(savedIndex.topK)
        assertThat(index.embeddingModel).isEqualTo(savedIndex.embeddingModel)
        assertThat(index.similarityMetric).isEqualTo(savedIndex.similarityMetric)
        assertThat(index.reranker).isEqualTo(savedIndex.reranker)
    }

    @Test
    @DisplayName("인덱스 상세 조회 시 없는 인덱스를 조회하면 에러가 터진다.")
    @Transactional
    fun getIndexTestWithInvalidIndex() {
        // given
        val savedIndex = indexRepository.save(
            Index(
                "test",
                1,
                0,
                SimilarityMetric.COSINE,
                1,
                EmbeddingModel.TEXT_EMBEDDING_3_LARGE,
                true
            )
        )

        // when * then
        val message = assertThrows<IndexException> { indexService.getIndex(savedIndex.id!! + 1L) }.message

        assertEquals(message, "리소스를 찾을 수 없습니다.")
    }

}