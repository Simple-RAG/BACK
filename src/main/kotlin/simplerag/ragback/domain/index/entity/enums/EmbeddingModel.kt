package simplerag.ragback.domain.index.entity.enums

enum class EmbeddingModel(val dim: Int) {
    // OpenAI
    TEXT_EMBEDDING_ADA_002(1536),
    TEXT_EMBEDDING_3_SMALL(1536),
    TEXT_EMBEDDING_3_LARGE(3072),

    // SBERT / HuggingFace
    ALL_MINILM_L6_V2(384),
    ALL_MP_NET_BASE_V2(768),
    MULTI_QA_MP_NET_BASE_DOT_V1(768),
    DISTILUSE_BASE_MULTILINGUAL_CASUAL(512),
    PARAPHRASE_MULTILINGUAL_MINILM_L12_V2(384),
    KO_SBERT_V1(768),
    KOR_SROBERTA(768),

    // Korean specific
    KPF_BERT_KOREAN(768),
    KOCSEBERT(768),
    BM_KO_SMALL(512),
    BM_KO_LARGE(1024),

    // Instructor / Mistral
    INSTRUCTOR_BASE(768),
    INSTRUCTOR_XL(1024),
    MISTRAL_EMBED(4096),

    // BGE / E5 etc
    BGE_SMALL_EN(384),
    BGE_BASE_EN(768),
    BGE_LARGE_EN(1024),
    BGE_M3(1024),
    E5_SMALL(384),
    E5_BASE(768),
    E5_LARGE(1024),

    // Old word vectors
    FASTTEXT_KO(300),
}
