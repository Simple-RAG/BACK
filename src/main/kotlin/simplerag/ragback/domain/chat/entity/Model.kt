package simplerag.ragback.domain.chat.entity

import jakarta.persistence.*
import simplerag.ragback.domain.index.entity.Index
import simplerag.ragback.domain.prompt.entity.Prompt
import simplerag.ragback.global.entity.BaseEntity

@Entity
@Table(name = "models")
class Model(

    @Column(name = "name", nullable = false, unique = true, length = 100)
    val name: String,

    @Column(name = "llm_model", nullable = false)
    val llmModel: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "indexes_id")
    val index: Index,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prompts_id")
    val prompt: Prompt,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "models_id")
    val id: Long? = null,
) : BaseEntity()