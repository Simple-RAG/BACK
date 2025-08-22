package simplerag.ragback.global.util

object TextChunker {
    fun chunkByCharsSeq(raw: String, size: Int, overlap: Int): Sequence<String> = sequence {
        require(size >= 1) { "chunk size must be >= 1" }
        require(overlap in 0 until size) { "overlap must be 0..size-1" }

        val text = normalize(raw)
        if (text.isBlank()) return@sequence
        if (text.length <= size) { yield(text); return@sequence }

        val step = size - overlap
        var start = 0
        while (start < text.length) {
            val end = (start + size).coerceAtMost(text.length)
            yield(text.substring(start, end))
            if (end == text.length) break
            start += step
        }
    }

    private fun normalize(s: String): String =
        s.replace("\r\n", "\n").replace("\r", "\n")
            .replace(Regex("[ \t]+"), " ")
            .replace(Regex("\\n{3,}"), "\n\n")
            .trim()
}
