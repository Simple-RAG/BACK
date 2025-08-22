package simplerag.ragback.domain.document.entity

import jakarta.persistence.*
import simplerag.ragback.global.entity.BaseEntity

@Entity
@Table(
    name = "data_files",
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

    @Column(nullable = false, length = 2048, name = "file_url")
    val fileUrl: String,
) : BaseEntity() {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "data_files_id")
    val id: Long = 0

    companion object {
        fun from(title: String, type: String, sizeBytes : Long, sha256 : String, fileUrl: String): DataFile {
            return DataFile(title, type, sizeBytes, sha256, fileUrl)
        }
    }
}
