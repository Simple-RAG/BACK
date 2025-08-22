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
    DISTILUSE_BASE_MULTILINGUAL_CASED_V2(512, "sentence-transformers/distiluse-base-multilingual-cased-v2"),

    // Korean
    KO_SBERT_V1(768, "jhgan/ko-sbert-v1"),

    // BGE
    BGE_BASE_EN(768, "BAAI/bge-base-en-v1.5"),
    BGE_M3(1024, "BAAI/bge-m3"),

    // E5
    E5_BASE(768, "intfloat/e5-base-v2"),

    // fake
    FAKE(1, "fake");

    companion object {
        fun findByModelId(modelId: String): EmbeddingModel? {
            return entries.find { it.modelId == modelId }
        }

        fun getAllModelIds(): List<String> {
            return entries.map { it.modelId }
        }
    }
}
