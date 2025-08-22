package simplerag.ragback.domain.document.entity

import jakarta.persistence.*
import simplerag.ragback.global.entity.BaseEntity

@Entity
@Table(
    name = "data_files_tags",
    uniqueConstraints = [UniqueConstraint(columnNames = ["data_files_id", "tags_id"])]
)
class DataFileTag(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tags_id")
    var tag: Tag,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_files_id")
    var dataFile: DataFile
) : BaseEntity() {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "data_files_tags_id")
    val id: Long = 0
}