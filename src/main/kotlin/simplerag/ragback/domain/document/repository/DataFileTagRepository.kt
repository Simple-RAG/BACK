package simplerag.ragback.domain.document.repository

import org.springframework.data.jpa.repository.JpaRepository
import simplerag.ragback.domain.document.entity.DataFile
import simplerag.ragback.domain.document.entity.DataFileTag

interface DataFileTagRepository : JpaRepository<DataFileTag, Long> {
    fun existsByDataFileIdAndTagId(dataFileId: Long, tagId: Long): Boolean

    fun findAllByDataFileIn(dataFiles: Collection<DataFile>): List<DataFileTag>

    fun deleteAllByDataFile(dataFile: DataFile)
}