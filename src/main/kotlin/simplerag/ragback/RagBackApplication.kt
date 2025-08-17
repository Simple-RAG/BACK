package simplerag.ragback

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class RagBackApplication

fun main(args: Array<String>) {
    runApplication<RagBackApplication>(*args)
}
