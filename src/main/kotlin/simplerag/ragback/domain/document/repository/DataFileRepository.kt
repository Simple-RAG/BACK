package simplerag.ragback.domain.document.repository

import org.springframework.data.jpa.repository.JpaRepository
import simplerag.ragback.domain.document.entity.DataFile

interface DataFileRepository : JpaRepository<DataFile, Long> {
    fun existsBySha256(sha256: String): Boolean
}