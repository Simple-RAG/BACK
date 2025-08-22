package simplerag.ragback.global.util.loader


interface ContentLoader {
    fun load(url: String): String
}