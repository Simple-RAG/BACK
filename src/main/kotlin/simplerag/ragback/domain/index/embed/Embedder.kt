package simplerag.ragback.domain.index.embed

interface Embedder {
    val dim: Int
    fun embed(text: String): FloatArray
}