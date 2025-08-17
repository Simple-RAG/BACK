package simplerag.ragback.domain.document.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import simplerag.ragback.domain.document.entity.DataFile
import simplerag.ragback.domain.document.entity.DataFileTag
import simplerag.ragback.domain.document.entity.Tag

interface DataFileTagRepository : JpaRepository<DataFileTag, Long> {
    fun existsByDataFileIdAndTagId(dataFileId: Long, tagId: Long): Boolean

    @Query("""
        SELECT DISTINCT t
        FROM DataFileTag dft
        JOIN dft.tag t
        WHERE dft.dataFile = :dataFile
    """)
    fun findTagsByDataFile(@Param("dataFile") dataFile: DataFile): List<Tag>

    fun deleteAllByDataFile(dataFile: DataFile)
}