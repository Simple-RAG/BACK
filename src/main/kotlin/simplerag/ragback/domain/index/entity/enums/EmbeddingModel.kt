package simplerag.ragback.domain.index.entity.enums

enum class EmbeddingModel(
    val dim: Int,
    val modelId: String
) {
    // OpenAI
    TEXT_EMBEDDING_3_SMALL(1536, "text-embedding-3-small"),
    TEXT_EMBEDDING_3_LARGE(3072, "text-embedding-3-large"),

    // SBERT / HuggingFace
    ALL_MINILM_L6_V2(384, "sentence-transformers/all-MiniLM-L6-v2"),
    ALL_MP_NET_BASE_V2(768, "sentence-transformers/all-mpnet-base-v2"),
    MULTI_QA_MP_NET_BASE_DOT_V1(768, "sentence-transformers/multi-qa-mpnet-base-dot-v1"),
    DISTILUSE_BASE_MULTILINGUAL_CASED_V2(512, "sentence-transformers/distiluse-base-multilingual-cased-v2"),
    PARAPHRASE_MULTILINGUAL_MINILM_L12_V2(384, "sentence-transformers/paraphrase-multilingual-MiniLM-L12-v2"),
    KO_SBERT_V1(768, "jhgan/ko-sbert-v1"),
    KOR_SROBERTA(768, "jhgan/ko-sroberta-medium-nli"),

    // Korean specific
    BM_KO_SMALL(512, "bespin-global/klue-sroberta-base-continue-learning-by-mnr"),

    // Instructor / Mistral
    INSTRUCTOR_BASE(768, "hkunlp/instructor-base"),
    INSTRUCTOR_XL(1024, "hkunlp/instructor-xl"),
    MISTRAL_EMBED(1024, "mistral-embed"),

    // BGE / E5 etc
    BGE_SMALL_EN(384, "BAAI/bge-small-en-v1.5"),
    BGE_BASE_EN(768, "BAAI/bge-base-en-v1.5"),
    BGE_LARGE_EN(1024, "BAAI/bge-large-en-v1.5"),
    BGE_M3(1024, "BAAI/bge-m3"),
    E5_SMALL(384, "intfloat/e5-small-v2"),
    E5_BASE(768, "intfloat/e5-base-v2"),
    E5_LARGE(1024, "intfloat/e5-large-v2"),

    // Old word vectors
    FASTTEXT_KO(300, "fasttext-ko-300d");

    companion object {
        fun findByModelId(modelId: String): EmbeddingModel? {
            return entries.find { it.modelId == modelId }
        }

        fun getAllModelIds(): List<String> {
            return entries.map { it.modelId }
        }
    }
}
