package simplerag.ragback.domain.document.entity

import jakarta.persistence.*

@Entity
@Table(
    name = "data_file_tags",
    uniqueConstraints = [UniqueConstraint(columnNames = ["data_file_id", "tag_id"])]
)
class DataFileTag(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    var tag: Tag,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_file_id", nullable = false)
    var dataFile: DataFile,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
)