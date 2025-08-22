package simplerag.ragback.global.util.loader

import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.nio.charset.StandardCharsets

@Component
class HttpContentLoader : ContentLoader {

    private val restTemplate: RestTemplate = RestTemplate().apply {
        // 기존 String 컨버터 제거 후 UTF-8 컨버터를 맨 앞에 추가
        val replaced = messageConverters.filterNot { it is StringHttpMessageConverter }.toMutableList()
        replaced.add(0, StringHttpMessageConverter(StandardCharsets.UTF_8))
        messageConverters = replaced
    }

    override fun load(url: String): String {
        return restTemplate.getForObject(url, String::class.java) ?: ""
    }
}