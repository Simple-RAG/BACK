package simplerag.ragback.domain.document.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import simplerag.ragback.domain.document.entity.DataFile
import simplerag.ragback.domain.document.entity.DataFileTag

interface DataFileTagRepository : JpaRepository<DataFileTag, Long> {
    fun existsByDataFileIdAndTagId(dataFileId: Long, tagId: Long): Boolean

    @Query(
        """
        SELECT dft
        FROM DataFileTag dft
        JOIN FETCH dft.tag t
        WHERE dft.dataFile = :dataFile
    """
    )
    fun findTagsByDataFile(@Param("dataFile") dataFile: DataFile): List<DataFileTag>

    fun deleteAllByDataFile(dataFile: DataFile)
}