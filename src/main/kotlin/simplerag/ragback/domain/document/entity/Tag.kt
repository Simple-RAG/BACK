package simplerag.ragback.domain.document.entity

import jakarta.persistence.*
import simplerag.ragback.global.entity.BaseEntity

@Entity
@Table(
    name = "tags",
    uniqueConstraints = [UniqueConstraint(columnNames = ["name"])]
)
class Tag(

    @Column(nullable = false, length = 60)
    val name: String,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tags_id")
    val id: Long? = null,
): BaseEntity()