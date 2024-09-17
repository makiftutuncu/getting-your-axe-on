export enum ShipType {
    Carrier = "Carrier",
    Battleship = "Battleship",
    Destroyer = "Destroyer",
    Submarine = "Submarine",
    PatrolBoat = "PatrolBoat"
}

export const shipTypeSizes: { [type in ShipType]: number } = {
    [ShipType.Carrier]: 5,
    [ShipType.Battleship]: 4,
    [ShipType.Destroyer]: 3,
    [ShipType.Submarine]: 3,
    [ShipType.PatrolBoat]: 2
}
