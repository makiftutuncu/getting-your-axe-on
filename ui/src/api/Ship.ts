import { Direction } from "./Direction"
import { ShipType } from "./ShipType"

export interface Ship {
    x: number
    y: number
    direction: Direction
    type: ShipType
}
