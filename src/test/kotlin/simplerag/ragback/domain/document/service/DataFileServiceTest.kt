package simplerag.ragback.domain.document.service

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import simplerag.ragback.domain.document.dto.DataFileBulkCreateRequest
import simplerag.ragback.domain.document.dto.DataFileBulkCreateRequest.DataFileCreateItem
import simplerag.ragback.domain.document.entity.DataFile
import simplerag.ragback.domain.document.repository.DataFileRepository
import simplerag.ragback.domain.document.repository.DataFileTagRepository
import simplerag.ragback.domain.document.repository.TagRepository
import simplerag.ragback.global.error.CustomException
import simplerag.ragback.global.error.ErrorCode
import simplerag.ragback.global.error.FileException
import simplerag.ragback.global.util.converter.sha256Hex

@SpringBootTest
@ActiveProfiles("test")
class DataFileServiceTest(
    @Autowired val dataFileService: DataFileService,
    @Autowired val dataFileRepository: DataFileRepository,
    @Autowired val tagRepository: TagRepository,
    @Autowired val dataFileTagRepository: DataFileTagRepository,
) {


    companion object {

        private val pgvectorImage = DockerImageName
            .parse("pgvector/pgvector:pg16")
            .asCompatibleSubstituteFor("postgres")

        @ServiceConnection
        val postgres: PostgreSQLContainer<*> =
            PostgreSQLContainer(pgvectorImage).apply {
                withInitScript("db/init.sql")
            }
    }


    @AfterEach
    fun clean() {
        dataFileTagRepository.deleteAll()
        tagRepository.deleteAll()
        dataFileRepository.deleteAll()
    }

    @Test
    @Transactional
    @DisplayName("업로드 시 잘 저장이 된다.")
    fun uploadOk() {
        // given
        val bytes = "hello world".toByteArray()
        val req = DataFileBulkCreateRequest(
            listOf(DataFileCreateItem(title = "greeting", tags = listOf(" ai ", "RAG", "ai")))
        )
        val f = file("greet.txt", bytes, contentType = "text/plain")

        // when
        val res = dataFileService.upload(listOf(f), req)

        // then
        assertEquals(1, res.dataFilePreviewResponseList.size)
        val r0 = res.dataFilePreviewResponseList.first()
        assertTrue(r0.id > 0)
        assertEquals(sha256Hex(bytes), r0.sha256)

        val saved = dataFileRepository.findById(r0.id).orElseThrow()
        assertEquals("greeting", saved.title)
        assertEquals("text/plain", saved.type)
        assertEquals(sha256Hex(bytes), saved.sha256)
        assertFalse(saved.content.isBlank())

        val ai = tagRepository.findByName("AI")
        val rag = tagRepository.findByName("RAG")
        assertNotNull(ai); assertNotNull(rag)
        assertTrue(dataFileTagRepository.existsByDataFileIdAndTagId(saved.id!!, ai!!.id!!))
        assertTrue(dataFileTagRepository.existsByDataFileIdAndTagId(saved.id!!, rag!!.id!!))
    }

    @Test
    @DisplayName("files와 items 개수 불일치 시 예외를 던진다.")
    fun invalidInputCountMismatch() {
        // given
        val req = DataFileBulkCreateRequest(items = emptyList())
        val f = file("a.txt", "a".toByteArray())

        // when
        val ex = assertThrows(CustomException::class.java) { dataFileService.upload(listOf(f), req) }

        // then
        assertEquals(ErrorCode.INVALID_INPUT, ex.errorCode)
    }

    @Test
    @Transactional
    @DisplayName("동일 sha256이 이미 있으면 업로드 거부한다.")
    fun rejectDuplicateSha() {
        // given
        val bytes = "same".toByteArray()
        val sha = sha256Hex(bytes)
        dataFileRepository.save(
            DataFile(
                title = "exists",
                type = "text/plain",
                sizeBytes = 0,
                sha256 = sha,
                content = "fake://original/exists.txt",
            )
        )
        val req = DataFileBulkCreateRequest(listOf(DataFileCreateItem("dup", listOf("tag"))))
        val f = file("dup.txt", bytes)

        // when
        val ex = assertThrows(FileException::class.java) { dataFileService.upload(listOf(f), req) }

        // then
        assertEquals(ErrorCode.ALREADY_FILE, ex.errorCode)
    }

    @Test
    @Transactional
    @DisplayName("컨텐츠 타입 지정되지 않을 거 일 시 에러가 난다")
    fun unknownTypeOctetStream() {
        // given
        val bytes = "x".toByteArray()
        val req = DataFileBulkCreateRequest(listOf(DataFileCreateItem("noext", emptyList())))
        val f = file(name = "noext", content = bytes, contentType = null) // no extension

        // when
        assertThrows(CustomException::class.java) { dataFileService.upload(listOf(f), req) }
            .message.equals("FILE TYPE이 유효하지 않습니다.")
    }

    @Test
    @Transactional
    @DisplayName("같은 파일 두 번 업로드 시 에러가 난다.")
    fun secondUploadAlreadyFile() {
        // given
        val bytes = "dupcontent".toByteArray()
        val req = DataFileBulkCreateRequest(listOf(DataFileCreateItem("t", emptyList())))
        val f1 = file("x.txt", bytes)
        val f2 = file("y.txt", bytes)

        // when
        dataFileService.upload(listOf(f1), req)
        val ex = assertThrows(FileException::class.java) { dataFileService.upload(listOf(f2), req) }

        // then
        assertEquals(ErrorCode.ALREADY_FILE, ex.errorCode)
    }

    @Test
    @DisplayName("데이터 조회가 잘 된다")
    @Transactional
    fun getDataFilesOK() {
        // given
        val bytes1 = "test1".toByteArray()
        val sha1 = sha256Hex(bytes1)
        val bytes2 = "test2".toByteArray()
        val sha2 = sha256Hex(bytes2)
        dataFileRepository.saveAll(
            listOf(
                DataFile(
                    title = "exists",
                    type = "text/plain",
                    sizeBytes = 0,
                    sha256 = sha1,
                    content = "fake://original/exists.txt",
                ),
                DataFile(
                    title = "exists2",
                    type = "text/pdf",
                    sizeBytes = 0,
                    sha256 = sha2,
                    content = "fake://original/exists.txt",
                )
            )
        )

        val cursor = 0L
        val take = 2

        // when
        val dataFiles = dataFileService.getDataFiles(cursor, take)

        // then
        val dataFileDetailResponse = dataFiles.dataFileDetailResponseList[0]
        assertEquals(dataFileDetailResponse.title, "exists")
        assertEquals(dataFileDetailResponse.type, "text/plain")
        assertEquals(dataFileDetailResponse.sizeMB, 0.0)
        assertEquals(dataFileDetailResponse.sha256, sha1)

        val dataFileDetailResponse2 = dataFiles.dataFileDetailResponseList[1]
        assertEquals(dataFileDetailResponse2.title, "exists2")
        assertEquals(dataFileDetailResponse2.type, "text/pdf")
        assertEquals(dataFileDetailResponse2.sizeMB, 0.0)
        assertEquals(dataFileDetailResponse2.sha256, sha2)

        assertEquals(dataFiles.cursor, dataFileDetailResponse2.id)
        assertEquals(dataFiles.hasNext, false)
    }

    // -----------------------
    // helpers
    // -----------------------

    private fun file(
        name: String,
        content: ByteArray,
        contentType: String? = "text/plain",
        paramName: String = "files"
    ): MultipartFile =
        MockMultipartFile(paramName, name, contentType, content)
}
