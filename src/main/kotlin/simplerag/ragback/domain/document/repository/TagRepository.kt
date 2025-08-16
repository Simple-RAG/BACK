package simplerag.ragback.domain.document.repository

import org.springframework.data.jpa.repository.JpaRepository
import simplerag.ragback.domain.document.entity.Tag
import java.util.*

interface TagRepository : JpaRepository<Tag, Long> {

    fun findByName(name: String): Tag?
}
