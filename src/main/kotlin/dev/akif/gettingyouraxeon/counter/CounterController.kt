package dev.akif.gettingyouraxeon.counter

import dev.akif.gettingyouraxeon.counter.query.Counter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/counters")
class CounterController(private val counters: CounterService) {
    @GetMapping
    suspend fun list(
        @RequestParam(required = false, defaultValue = "0")
        page: Int,
        @RequestParam(required = false, defaultValue = "10")
        size: Int
    ): Page<CounterResponse> =
        counters.list(page, size).map { it.toResponse() }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun create(name: String): CounterResponse =
        counters.create(name).toResponse()

    @GetMapping("/{id}")
    suspend fun get(@PathVariable id: UUID): CounterResponse =
        counters.get(id).toResponse()

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun delete(@PathVariable id: UUID) {
        counters.delete(id)
    }

    @PutMapping("/{id}/value")
    suspend fun change(@PathVariable id: UUID, adjustment: Int, @RequestParam(required = false) name: String?): CounterResponse =
        counters.change(id, adjustment, name).toResponse()

    @GetMapping("/{id}/watch", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun watch(@PathVariable id: UUID): Flow<CounterResponse> =
        counters.watch(id).map { it.toResponse() }

    private fun Counter.toResponse() = CounterResponse(value, createdAt, updatedAt)
}
