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

    @Column(name = "llm_model", nullable = false, unique = true)
    val llmModel: String,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "indexes_id", nullable = false, unique = true)
    val index: Index,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prompts_id", nullable = false, unique = true)
    val prompt: Prompt,

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "models_id")
    val id: Long? = null,
): BaseEntity()