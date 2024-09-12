package dev.akif.gettingyouraxeon.counter.query

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CounterRepository: JpaRepository<Counter, String>
