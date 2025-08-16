package simplerag.ragback.domain.document.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import simplerag.ragback.domain.document.dto.DataFileBulkCreateRequest
import simplerag.ragback.domain.document.dto.DataFileCreateItem
import simplerag.ragback.domain.document.entity.DataFile
import simplerag.ragback.domain.document.repository.DataFileRepository
import simplerag.ragback.domain.document.repository.DataFileTagRepository
import simplerag.ragback.domain.document.repository.TagRepository
import simplerag.ragback.global.error.CustomException
import simplerag.ragback.global.error.ErrorCode
import simplerag.ragback.global.error.FileException
import java.security.MessageDigest
import java.time.LocalDateTime

@SpringBootTest
@ActiveProfiles("test")
class DataFileServiceTest(
    @Autowired private val service: DataFileService,
    @Autowired private val dataFileRepository: DataFileRepository,
    @Autowired private val tagRepository: TagRepository,
    @Autowired private val dataFileTagRepository: DataFileTagRepository,
) {

    @Test
    @Transactional
    @DisplayName("업로드 시 잘 저장이 된다.")
    fun uploadOk() {
        // given
        val bytes = "hello world".toByteArray()
        val req = DataFileBulkCreateRequest(
            listOf(DataFileCreateItem(title = "greeting", tags = listOf(" ai ", "rag", "ai")))
        )
        val f = file("greet.txt", bytes, contentType = "text/plain")

        // when
        val res = service.upload(listOf(f), req)

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

        val ai = tagRepository.findByName("ai")
        val rag = tagRepository.findByName("rag")
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
        val ex = assertThrows(CustomException::class.java) { service.upload(listOf(f), req) }

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
        val now = LocalDateTime.now()
        dataFileRepository.save(
            DataFile(
                title = "exists",
                type = "text/plain",
                sizeBytes = 0,
                sha256 = sha,
                fileUrl = "fake://original/exists.txt",
                updatedAt = now,
                createdAt = now
            )
        )
        val req = DataFileBulkCreateRequest(listOf(DataFileCreateItem("dup", listOf("tag"))))
        val f = file("dup.txt", bytes)

        // when
        val ex = assertThrows(FileException::class.java) { service.upload(listOf(f), req) }

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
        val res = service.upload(listOf(f), req)

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
        service.upload(listOf(f1), req)
        val ex = assertThrows(FileException::class.java) { service.upload(listOf(f2), req) }

        // then
        assertEquals(ErrorCode.ALREADY_FILE, ex.errorCode)
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

    private fun sha256Hex(bytes: ByteArray): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }
}
