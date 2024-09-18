import Cell from './Cell'
import { Game } from '../api/Game'
import { Table, TableBody, TableContainer, TableHead, TableRow } from '@mui/material'
import { Player } from '../api/Player'
import { GameStatus } from '../api/GameStatus'

type Props = {
    game: Game
    player: Player
    isOpponent: boolean
    onClick: (x: number, y: number) => void
    getContent: (cell: string) => string
    isSelected: (x: number, y: number) => boolean
}

const Board = ({ game, player, isOpponent, onClick, isSelected }: Props) => {
    return <TableContainer>
        <Table size="small" padding="none">
            <TableHead>
                <TableRow>
                    <Cell x={-1} y={-1} content="" />
                    {[0, 1, 2, 3, 4, 5, 6, 7, 8, 9].map((i) =>
                        <Cell key={i} x={-1} y={-1} content={`${i}`} />
                    )}
                </TableRow>
            </TableHead>
            <TableBody>
                {
                    ((player && game.boards[player]) || Array<string[]>(10).fill(Array<string>(10).fill(" "))).map((row, y) => (
                        <TableRow key={y}>
                            <Cell x={-1} y={-1} content={`${y}`} />
                            {
                                row.map((cell, x) => (
                                    <Cell
                                        key={x}
                                        x={x}
                                        y={y}
                                        content={
                                            isOpponent ? (
                                                cell === "X" || cell === "O" ? cell : " "
                                            ) : (
                                                (cell === "A" && player !== Player.A) || (cell === "B" && player !== Player.B) ? " " : cell
                                            )
                                        }
                                        enabled={(!isOpponent && game.status === GameStatus.Created) || (game.status === GameStatus.Started && (isOpponent && game.turn !== player))}
                                        selected={isSelected(x, y)}
                                        onClick={() => onClick(x, y)} />
                                ))
                            }
                        </TableRow>
                    ))
                }
            </TableBody>
        </Table>
    </TableContainer>
}

export default Board
