package simplerag.ragback.domain.index.entity.enums

enum class SimilarityMetric(
    val description: String
) {
    COSINE("코사인 유사도"),
    EUCLIDEAN("유클리드 거리"),
    DOT_PRODUCT("내적 유사도")
}