package dev.akif.gettingyouraxeon

import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer

@Configuration
class CorsConfiguration : WebFluxConfigurer {
    override fun addCorsMappings(corsRegistry: CorsRegistry) {
        corsRegistry
            .addMapping("/**")
            .allowedOrigins("http://localhost:8080", "http://localhost:3000", "http://localhost:5173")
            .allowedMethods("*")
            .exposedHeaders("*")
    }
}
