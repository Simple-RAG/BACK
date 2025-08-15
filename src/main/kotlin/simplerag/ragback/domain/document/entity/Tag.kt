package simplerag.ragback.domain.document.entity

import jakarta.persistence.*

@Entity
@Table(
    name = "tags",
    uniqueConstraints = [UniqueConstraint(columnNames = ["name"])]
)
data class Tag(

    @Column(nullable = false, length = 60)
    val name: String,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
)