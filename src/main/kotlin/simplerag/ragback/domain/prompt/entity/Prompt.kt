package simplerag.ragback.domain.prompt.entity

import jakarta.persistence.*
import simplerag.ragback.domain.prompt.entity.enums.PreSet
import simplerag.ragback.global.entity.BaseEntity

@Entity
@Table(name = "prompt")
class Prompt(

    @Column(name = "name", length = 100, nullable = false)
    val name: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "pre_set", nullable = false)
    val preSet: PreSet,

    @Column(name = "system_prompt", nullable = false, columnDefinition = "TEXT")
    val systemPrompt: String,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prompt_id")
    val id: Long? = null,
): BaseEntity()