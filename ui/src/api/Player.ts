export enum Player {
    A = "A",
    B = "B"
}

export const playerShips: { [player in Player]: string } = {
    [Player.A]: "🚢",
    [Player.B]: "⛴️"
}
