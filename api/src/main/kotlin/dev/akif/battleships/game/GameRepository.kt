package dev.akif.battleships.game

import dev.akif.battleships.query.GameEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GameRepository: JpaRepository<GameEntity, Long>
