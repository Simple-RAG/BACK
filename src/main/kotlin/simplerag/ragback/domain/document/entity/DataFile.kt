package simplerag.ragback.domain.document.entity

import jakarta.persistence.*
import simplerag.ragback.global.entity.BaseEntity
import java.time.LocalDateTime

@Entity
@Table(
    name = "data_file",
    uniqueConstraints = [UniqueConstraint(columnNames = ["sha256"])]
)
class DataFile(

    @Column(nullable = false, length = 255)
    val title: String,

    @Column(name = "file_type", nullable = false, length = 120)
    val type: String,

    @Column(name = "size_bytes", nullable = false)
    val sizeBytes: Long,

    @Column(nullable = false, length = 64)
    val sha256: String,

    @Column(nullable = false, length = 2048)
    val fileUrl: String,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "data_file_id")
    val id: Long? = null,
) : BaseEntity()
