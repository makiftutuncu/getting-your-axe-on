package dev.akif.gettingyouraxeon.counter

import dev.akif.gettingyouraxeon.counter.api.CounterAdjustment
import dev.akif.gettingyouraxeon.counter.api.CounterResponse
import dev.akif.gettingyouraxeon.counter.query.Counter
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/counters")
class CounterController(private val counters: CounterService) {
    @GetMapping
    fun list(
        @RequestParam(required = false, defaultValue = "0")
        page: Int,
        @RequestParam(required = false, defaultValue = "10")
        size: Int
    ): Flux<CounterResponse> =
        counters.list(page, size).map { it.toResponse() }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestParam name: String): Mono<Void> =
        counters.create(name)

    @GetMapping("/{name}")
    fun get(@PathVariable name: String): Mono<CounterResponse> =
        counters.get(name).map { it.toResponse() }

    @DeleteMapping("/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable name: String): Mono<Void> =
        counters.delete(name)

    @PutMapping("/{name}")
    fun change(
        @PathVariable name: String,
        @RequestBody adjustment: CounterAdjustment
    ): Mono<CounterResponse> =
        counters.change(name, adjustment).map { it.toResponse() }

    @GetMapping("/{name}/watch", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun watch(@PathVariable name: String): Flux<CounterResponse> =
        counters.watch(name).map { it.toResponse() }

    private fun Counter.toResponse() = CounterResponse(name, value, createdAt, updatedAt)
}
