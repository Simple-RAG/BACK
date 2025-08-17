package simplerag.ragback.domain.document.entity

import jakarta.persistence.*
import simplerag.ragback.global.entity.BaseEntity

@Entity
@Table(
    name = "data_files_tags",
    uniqueConstraints = [UniqueConstraint(columnNames = ["data_file_id", "tag_id"])]
)
class DataFileTag(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tags_id", nullable = false)
    var tag: Tag,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_files_id", nullable = false)
    var dataFile: DataFile,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "data_files_tags_id")
    val id: Long? = null,
): BaseEntity()