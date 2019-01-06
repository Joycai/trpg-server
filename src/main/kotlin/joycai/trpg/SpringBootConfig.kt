package joycai.trpg

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


/**
 * spring boot配置.
 */
@SpringBootApplication
@EnableAspectJAutoProxy
class SpringBootConfig : SpringBootServletInitializer() {

    val myLogger: Logger = LoggerFactory.getLogger(SpringBootConfig::class.java)

    override fun configure(builder: SpringApplicationBuilder): SpringApplicationBuilder {
        return builder.sources(SpringBootConfig::class.java)
    }

    /**
     * cors
     */
    @Bean
    fun corsConfigurer(): WebMvcConfigurer {
        return object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/**")
            }
        }
    }

    @Bean
    fun objectMapper():ObjectMapper{
        val objectMapper = ObjectMapper()
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)
        objectMapper.registerModule(KotlinModule())
        return objectMapper
    }
}