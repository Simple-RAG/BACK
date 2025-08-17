package simplerag.ragback.domain.document.repository

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import simplerag.ragback.domain.document.entity.DataFile

interface DataFileRepository : JpaRepository<DataFile, Long> {
    fun existsBySha256(sha256: String): Boolean

    fun findByOrderById(pageable: Pageable): Slice<DataFile>
}