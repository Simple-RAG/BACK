package simplerag.ragback.domain.prompt.entity

import jakarta.persistence.*
import simplerag.ragback.global.entity.BaseEntity

@Entity
@Table(name = "few_shot")
class FewShot(

    @Column(name = "question", nullable = false, length = 255)
    val question: String,

    @Column(name = "answer", nullable = false, columnDefinition = "TEXT")
    val answer: String,

    @Column(name = "evidence", nullable = false, columnDefinition = "TEXT")
    val evidence: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prompt_id", nullable = false)
    val prompt: Prompt,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "few_shot_id")
    val id: Long? = null,
): BaseEntity()