package simplerag.ragback

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RagBackApplication

fun main(args: Array<String>) {
    runApplication<RagBackApplication>(*args)
}
