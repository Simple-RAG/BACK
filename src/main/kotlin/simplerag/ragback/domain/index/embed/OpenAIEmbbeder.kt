package simplerag.ragback.domain.index.embed

import org.springframework.ai.openai.OpenAiEmbeddingModel
import org.springframework.stereotype.Component

@Component
class OpenAIEmbedder(
    private val openAiEmbeddingModel: OpenAiEmbeddingModel
) : Embedder {
    override val dim: Int = 1536
    override fun embed(text: String): FloatArray =
        openAiEmbeddingModel.embed(text)
}
