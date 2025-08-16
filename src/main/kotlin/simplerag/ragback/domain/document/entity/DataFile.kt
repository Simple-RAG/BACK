package simplerag.ragback.domain.document.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "data_file",
    uniqueConstraints = [UniqueConstraint(columnNames = ["sha256"])]
)
class DataFile(

    @Column(nullable = false, length = 255)
    val title: String,

    @Column(nullable = false, length = 120)
    val type: String,

    @Column(name = "size_mb", nullable = false)
    val sizeBytes: Long,

    @Column(nullable = false, length = 128)
    val sha256: String,

    val fileUrl: String,

    val updatedAt: LocalDateTime,

    val createdAt: LocalDateTime,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
)
