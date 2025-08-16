package simplerag.ragback.domain.document.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import simplerag.ragback.domain.document.dto.DataFileBulkCreateRequest
import simplerag.ragback.domain.document.dto.DataFileResponse
import simplerag.ragback.domain.document.dto.DataFileResponseList
import simplerag.ragback.domain.document.entity.DataFile
import simplerag.ragback.domain.document.entity.DataFileTag
import simplerag.ragback.domain.document.entity.Tag
import simplerag.ragback.domain.document.repository.DataFileRepository
import simplerag.ragback.domain.document.repository.DataFileTagRepository
import simplerag.ragback.domain.document.repository.TagRepository
import simplerag.ragback.global.error.CustomException
import simplerag.ragback.global.error.ErrorCode
import simplerag.ragback.global.error.FileException
import simplerag.ragback.global.util.S3Type
import simplerag.ragback.global.util.S3Util
import java.security.MessageDigest
import java.time.LocalDateTime

@Service
class DataFileService(
    private val dataFileRepository: DataFileRepository,
    private val tagRepository: TagRepository,
    private val dataFileTagRepository: DataFileTagRepository,
    private val s3Util: S3Util,
) {

    @Transactional
    fun upload(
        files: List<MultipartFile>,
        req: DataFileBulkCreateRequest
    ): DataFileResponseList {
        if (files.isEmpty() || files.size != req.items.size) {
            throw CustomException(ErrorCode.INVALID_INPUT)
        }

        val now = LocalDateTime.now()

        val responses = files.mapIndexed { idx, file ->
            val meta = req.items[idx]
            val bytes = file.bytes
            val sha256 = sha256Hex(bytes)

            val sizeMb = kotlin.math.round((bytes.size / (1024.0 * 1024.0)) * 1000) / 1000.0
            val type = file.contentType
                ?: file.originalFilename?.substringAfterLast('.', "application/octet-stream")
                ?: "application/octet-stream"

            if (dataFileRepository.existsBySha256(sha256)) {
                throw FileException(ErrorCode.ALREADY_FILE, meta.title)
            }

            val fileUrl = s3Util.upload(file, S3Type.ORIGINAL_FILE)

            val dataFile = dataFileRepository.save(
                DataFile(meta.title, type, sizeMb, sha256, fileUrl, now, now)
            )

            val tags = getOrCreateTags(meta.tags)
            attachTagsIfMissing(dataFile, tags)

            DataFileResponse(requireNotNull(dataFile.id), dataFile.sha256)
        }

        return DataFileResponseList(responses)
    }

    private fun getOrCreateTags(names: List<String>): List<Tag> =
        names.mapNotNull { it.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
            .map { name ->
                tagRepository.findByName(name).orElseGet { tagRepository.save(Tag(name = name)) }
            }

    private fun attachTagsIfMissing(dataFile: DataFile, tags: List<Tag>) {
        val fileId = dataFile.id ?: return
        tags.forEach { tag ->
            val tagId = tag.id ?: return@forEach
            val exists = dataFileTagRepository.existsByDataFileIdAndTagId(fileId, tagId)
            if (!exists) {
                dataFileTagRepository.save(DataFileTag(tag = tag, dataFile = dataFile))
            }
        }
    }

    private fun sha256Hex(bytes: ByteArray): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }

}
