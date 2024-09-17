import { GameStatus } from "./GameStatus"
import { Player } from "./Player"
import { Ship } from "./Ship"

export interface Game {
    id: number
    boards: { [player in Player]: string[][] },
    ships: { [player in Player]: Ship[] },
    status: GameStatus,
    turn: Player,
    winner: Player | null,
    createdAt: Date,
    updatedAt: Date,
    startedAt: Date | null,
    finishedAt: Date | null
}
