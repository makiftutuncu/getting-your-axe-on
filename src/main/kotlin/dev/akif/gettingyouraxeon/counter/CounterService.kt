package dev.akif.gettingyouraxeon.counter

import dev.akif.gettingyouraxeon.counter.api.*
import dev.akif.gettingyouraxeon.counter.query.Counter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CounterService(
    private val commands: ReactorCommandGateway,
    private val queries: ReactorQueryGateway
) {
    suspend fun list(page: Int, size: Int): Page<Counter> =
        queries
            .query(ListCountersQuery(page, size), Page::class.java)
            .awaitSingle()
            .map { it as Counter }

    suspend fun create(name: String): Counter =
        commands
            .send<Counter>(CreateCounterCommand(name))
            .awaitSingle()

    suspend fun get(id: UUID): Counter =
        queries
            .query(GetCounterQuery(id), Counter::class.java)
            .awaitSingleOrNull()
            ?: throw CounterNotFoundException(id)

    suspend fun delete(id: UUID) {
        commands
            .send<Unit>(DeleteCounterCommand(id))
            .awaitSingle()
    }

    suspend fun change(id: UUID, adjustment: Int, name: String?): Counter =
        commands
            .send<Counter>(ChangeCounterCommand(id, name, adjustment))
            .awaitSingle()

    fun watch(id: UUID): Flow<Counter> =
        queries
            .streamingQuery(WatchCounterQuery(id), Counter::class.java)
            .asFlow()
}
