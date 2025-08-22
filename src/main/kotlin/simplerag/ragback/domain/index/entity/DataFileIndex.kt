package simplerag.ragback.domain.index.entity

import jakarta.persistence.*
import simplerag.ragback.domain.document.entity.DataFile
import simplerag.ragback.global.entity.BaseEntity

@Entity
@Table(name = "data_files_indexes")
class DataFileIndex(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_files_id", nullable = false)
    val dataFile: DataFile,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "indexes_id", nullable = false)
    val index: Index,

) : BaseEntity() {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "data_files_indexes_id")
    val id: Long = 0

}