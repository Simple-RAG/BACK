package simplerag.ragback.domain.index.embed

import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Primary
@Profile("test")
class FakeEmbder: Embedder {
    override val dim: Int = 1536
    override fun embed(text: String): FloatArray {
        return FloatArray(1536) { 0.0f }
    }
}