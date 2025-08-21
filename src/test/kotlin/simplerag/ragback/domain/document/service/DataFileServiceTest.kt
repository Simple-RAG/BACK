package simplerag.ragback.domain.document.service

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import org.springframework.web.multipart.MultipartFile
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import simplerag.ragback.domain.document.dto.DataFileBulkCreateRequest
import simplerag.ragback.domain.document.dto.DataFileCreateItem
import simplerag.ragback.domain.document.entity.DataFile
import simplerag.ragback.domain.document.repository.DataFileRepository
import simplerag.ragback.domain.document.repository.DataFileTagRepository
import simplerag.ragback.domain.document.repository.TagRepository
import simplerag.ragback.global.error.CustomException
import simplerag.ragback.global.error.ErrorCode
import simplerag.ragback.global.error.FileException
import simplerag.ragback.global.storage.FakeS3Util
import simplerag.ragback.global.util.S3Type
import simplerag.ragback.global.util.sha256Hex
import java.security.MessageDigest

@SpringBootTest
@ActiveProfiles("test")
class DataFileServiceTest(
    @Autowired private val dataFileService: DataFileService,
    @Autowired private val dataFileRepository: DataFileRepository,
    @Autowired private val tagRepository: TagRepository,
    @Autowired private val dataFileTagRepository: DataFileTagRepository,
    @Autowired private val s3Util: FakeS3Util
) {

    companion object {
        @Container
        @ServiceConnection
        val postgres = PostgreSQLContainer("postgres:15.3")
    }

    @Autowired
    lateinit var txManager: org.springframework.transaction.PlatformTransactionManager

    private fun txTemplate() = TransactionTemplate(txManager)

    @AfterEach
    fun clean() {
        dataFileTagRepository.deleteAll()
        tagRepository.deleteAll()
        dataFileRepository.deleteAll()
        s3Util.clear()
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
        assertFalse(saved.fileUrl.isNullOrBlank())

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
                fileUrl = "fake://original/exists.txt",
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
    @DisplayName("컨텐츠 타입 null이거나 확장자 없을 시 application/octet-stream 저장이 된다")
    fun unknownTypeOctetStream() {
        // given
        val bytes = "x".toByteArray()
        val req = DataFileBulkCreateRequest(listOf(DataFileCreateItem("noext", emptyList())))
        val f = file(name = "noext", content = bytes, contentType = null) // no extension

        // when
        val res = dataFileService.upload(listOf(f), req)

        // then
        val saved = dataFileRepository.findById(res.dataFilePreviewResponseList.first().id).orElseThrow()
        assertEquals("application/octet-stream", saved.type)
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
    @DisplayName("트랜잭션이 커밋되면 DB와 S3에 정상 저장된다")
    fun uploadCommitPersist() {
        // given
        val bytes = "commit-case".toByteArray()
        val req = DataFileBulkCreateRequest(
            listOf(DataFileCreateItem(title = "commit-title", tags = listOf("t1")))
        )
        val f = file("c.txt", bytes)

        // when
        val resultIds = txTemplate().execute {
            val res = dataFileService.upload(listOf(f), req)
            res.dataFilePreviewResponseList.map { it.id }
        }!!

        // then (DB)
        assertEquals(1, resultIds.size)
        val saved = dataFileRepository.findById(resultIds.first()).orElseThrow()
        assertEquals("commit-title", saved.title)
        assertEquals(sha256Hex(bytes), saved.sha256)
        assertFalse(saved.fileUrl.isNullOrBlank())

        // then (S3 - FakeS3Util 기준)
        assertTrue(s3Util.exists(saved.fileUrl!!), "커밋 시 S3에 파일이 존재해야 합니다")
    }


    @Test
    @DisplayName("파일 업로드 중 트랜잭션이 롤백되면 DB와 S3에서 모두 정리된다")
    fun uploadRollbackCleansDBandS3() {
        // given
        val bytes = "rollback-case".toByteArray()
        val filename = "r.txt"
        val req = DataFileBulkCreateRequest(
            listOf(DataFileCreateItem(title = "rollback-title", tags = listOf("t2")))
        )
        val f = file(filename, bytes)

        val hash12 = MessageDigest.getInstance("SHA-256")
            .digest(bytes).joinToString("") { "%02x".format(it) }
            .take(12)
        val expectedKey = "${S3Type.ORIGINAL_FILE.label}/${hash12}_$filename"
        val expectedUrl = s3Util.urlFromKey(expectedKey)

        // when: 트랜잭션 내에서 업로드 후 강제 롤백
        txTemplate().execute { status ->
            dataFileService.upload(listOf(f), req)
            status!!.setRollbackOnly()
        }

        // then
        val sha = sha256Hex(bytes)
        val existsInDb = dataFileRepository.findAll().any { it.sha256 == sha }
        assertFalse(existsInDb, "롤백되었으므로 DB에 남으면 안 됩니다")

        assertFalse(s3Util.exists(expectedUrl), "롤백 시 S3도 보상 삭제되어야 합니다")
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
                    fileUrl = "fake://original/exists.txt",
                ),
                DataFile(
                    title = "exists2",
                    type = "text/pdf",
                    sizeBytes = 0,
                    sha256 = sha2,
                    fileUrl = "fake://original/exists.txt",
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
