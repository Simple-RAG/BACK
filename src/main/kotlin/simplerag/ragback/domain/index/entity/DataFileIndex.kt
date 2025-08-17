package simplerag.ragback.domain.index.entity

import jakarta.persistence.*
import simplerag.ragback.domain.document.entity.DataFile
import simplerag.ragback.global.entity.BaseEntity

@Entity
@Table(name = "data_file_index")
class DataFileIndex(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_file_id", nullable = false)
    val dataFile: DataFile,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "indexes_id", nullable = false)
    val index: Index,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "data_file_index_id")
    val id: Long? = null,
): BaseEntity()