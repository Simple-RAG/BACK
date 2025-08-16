package simplerag.ragback.domain.document.service

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager
import org.springframework.web.multipart.MultipartFile
import simplerag.ragback.domain.document.dto.DataFileBulkCreateRequest
import simplerag.ragback.domain.document.dto.DataFilePreviewResponse
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
import simplerag.ragback.global.util.computeMetricsStreaming
import simplerag.ragback.global.util.resolveContentType
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
        val uploadedUrls = mutableListOf<String>()

        registerRollbackCleanup(uploadedUrls)

        val responses = files.mapIndexed { idx, file ->
            val meta = req.items[idx]

            val metrics = file.computeMetricsStreaming()
            val sha256 = metrics.sha256
            val sizeByte = metrics.sizeByte
            val type = file.resolveContentType()

            if (dataFileRepository.existsBySha256(sha256)) {
                throw FileException(ErrorCode.ALREADY_FILE, sha256)
            }

            val fileUrl = s3Util.upload(file, S3Type.ORIGINAL_FILE)
            uploadedUrls += fileUrl

            val dataFile = try {
                dataFileRepository.save(DataFile(meta.title, type, sizeByte, sha256, fileUrl, now, now))
            } catch (ex: DataIntegrityViolationException) {
                throw FileException(ErrorCode.ALREADY_FILE, sha256)
            }

            val tags = getOrCreateTags(meta.tags)
            attachTagsIfMissing(dataFile, tags)

            DataFilePreviewResponse(requireNotNull(dataFile.id), dataFile.sha256)
        }

        return DataFileResponseList(responses)
    }

    private fun registerRollbackCleanup(uploadedUrls: MutableList<String>) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(object : TransactionSynchronization {
                override fun afterCompletion(status: Int) {
                    if (status == TransactionSynchronization.STATUS_ROLLED_BACK) {
                        uploadedUrls.forEach { runCatching { s3Util.deleteByUrl(it) } }
                    }
                }
            })
        }
    }


    private fun getOrCreateTags(names: List<String>): List<Tag> =
        names.map { it.trim().uppercase() }
            .filter { it.isNotEmpty() }
            .distinct()
            .map { name ->
                tagRepository.findByName(name) ?: tagRepository.save(Tag(name = name))
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

}
