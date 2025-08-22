package simplerag.ragback.domain.document.service

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager
import org.springframework.web.multipart.MultipartFile
import simplerag.ragback.domain.document.dto.*
import simplerag.ragback.domain.document.entity.DataFile
import simplerag.ragback.domain.document.entity.DataFileTag
import simplerag.ragback.domain.document.entity.Tag
import simplerag.ragback.domain.document.repository.DataFileRepository
import simplerag.ragback.domain.document.repository.DataFileTagRepository
import simplerag.ragback.domain.document.repository.TagRepository
import simplerag.ragback.global.error.CustomException
import simplerag.ragback.global.error.ErrorCode
import simplerag.ragback.global.error.FileException
import simplerag.ragback.global.util.s3.S3Type
import simplerag.ragback.global.util.s3.S3Util
import simplerag.ragback.global.util.converter.computeMetricsStreaming
import simplerag.ragback.global.util.converter.resolveContentType
import java.util.*

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
    ): DataFilePreviewResponseList {
        if (files.isEmpty() || files.size != req.items.size) {
            throw CustomException(ErrorCode.INVALID_INPUT)
        }

        val uploadedUrls = mutableListOf<String>()
        registerRollbackCleanup(uploadedUrls)

        val responses = files.mapIndexed { idx, file ->
            val meta = req.items[idx]
            val metrics = file.computeMetricsStreaming()
            val sha256 = metrics.sha256
            val sizeBytes = metrics.sizeByte
            val type = file.resolveContentType()

            if (dataFileRepository.existsBySha256(sha256)) {
                throw FileException(ErrorCode.ALREADY_FILE, sha256)
            }

            val fileUrl = s3Util.upload(file, S3Type.ORIGINAL_FILE)
            uploadedUrls += fileUrl

            val dataFile = try {
                dataFileRepository.save(DataFile.from(meta.title, type, sizeBytes, sha256, fileUrl))
            } catch (ex: DataIntegrityViolationException) {
                throw FileException(ErrorCode.ALREADY_FILE, sha256)
            }

            val tags = getOrCreateTags(meta.tags)
            attachTagsIfMissing(dataFile, tags)

            return@mapIndexed DataFilePreviewResponse.from(dataFile)
        }

        return DataFilePreviewResponseList(responses)
    }

    @Transactional(readOnly = true)
    fun getDataFiles(
        cursor: Long,
        take: Int
    ): DataFileDetailResponseList {
        val files = dataFileRepository.findByIdGreaterThanOrderById(cursor, PageRequest.of(0, take))

        val allLinks = dataFileTagRepository.findAllByDataFileIn(files.content)
        val tagsByFileId: Map<Long, List<TagDTO>> =
            allLinks.groupBy(
                { requireNotNull(it.dataFile.id) { "DataFile.id is null" } }
            ).mapValues { (_, links) -> TagDTO.from(links) }

        val nextCursor = files.content.lastOrNull()?.id

        return DataFileDetailResponseList.from(files.content, tagsByFileId, nextCursor, files.hasNext())
    }

    @Transactional
    fun deleteFile(dataFilesId: Long) {
        val dataFile = dataFileRepository.findDataFileById(dataFilesId)
            ?: throw FileException(
                ErrorCode.NOT_FOUND,
                dataFilesId.toString()
            )

        dataFileTagRepository.deleteAllByDataFile(dataFile)

        dataFileRepository.delete(dataFile)
    }

    private fun registerRollbackCleanup(uploadedUrls: List<String>) {
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


    private fun getOrCreateTags(names: List<String>): List<Tag> {
        val normalized = names
            .map { it.trim().uppercase(Locale.ROOT) }
            .filter { it.isNotEmpty() }
            .distinct()

        if (normalized.isEmpty()) return emptyList()

        val existing = tagRepository.findByNameIn(normalized)
        val existingByName = existing.associateBy { it.name }

        val toCreate = normalized
            .asSequence()
            .filter { it !in existingByName }
            .map { Tag(name = it) }
            .toList()

        val created = if (toCreate.isNotEmpty()) {
            try {
                tagRepository.saveAllAndFlush(toCreate)
            } catch (ex: DataIntegrityViolationException) {
                tagRepository.findByNameIn(toCreate.map { it.name })
            }
        } else emptyList()

        return existing + created
    }


    private fun attachTagsIfMissing(dataFile: DataFile, tags: List<Tag>) {
        val temp = tags.mapNotNull { tag ->
            val exists = dataFileTagRepository.existsByDataFileIdAndTagId(dataFile.id, tag.id)

            if (exists) return@mapNotNull null

            return@mapNotNull DataFileTag(tag = tag, dataFile = dataFile)
        }

        dataFileTagRepository.saveAll(temp)
    }
}
