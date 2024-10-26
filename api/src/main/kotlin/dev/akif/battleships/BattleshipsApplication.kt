package dev.akif.battleships

import org.axonframework.common.jpa.EntityManagerProvider
import org.axonframework.config.EventProcessingConfigurer
import org.axonframework.eventhandling.tokenstore.jpa.JpaTokenStore
import org.axonframework.serialization.Serializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BattleshipsApplication {
    @Autowired
    fun configureTokenStore(
        configurer: EventProcessingConfigurer,
        entityManagerProvider: EntityManagerProvider,
        serializer: Serializer
    ) {
        configurer.registerTokenStore {
            JpaTokenStore
                .builder()
                .entityManagerProvider(entityManagerProvider)
                .serializer(serializer)
                .build()
        }
    }
}

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<BattleshipsApplication>(*args)
}
