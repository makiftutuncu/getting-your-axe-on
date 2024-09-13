package dev.akif.gettingyouraxeon

import org.axonframework.config.EventProcessingConfigurer
import org.axonframework.eventhandling.tokenstore.inmemory.InMemoryTokenStore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class GettingYourAxeOnApplication {
    @Autowired
    fun configureInMemoryTokenStore(configurer: EventProcessingConfigurer) {
        configurer.registerTokenStore { InMemoryTokenStore() }
    }
}

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<GettingYourAxeOnApplication>(*args)
}
