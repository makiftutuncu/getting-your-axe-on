import { Game } from "../api/Game"
import { GameStatus } from "../api/GameStatus"
import { Player } from "../api/Player"
import { Box, Button, Card, CardActions, CardContent, Chip, Divider, Table, TableBody, TableCell, TableContainer, TableRow, Typography } from "@mui/material"

type Props = {
    game: Game
}

const playerToUse = (game: Game): Player =>
    game.boards[Player.A] === undefined ? Player.A : Player.B

const GameCard = ({ game }: Props) => {
    const date = (d: Date): string => d.toString()

    return (
        <Card sx={{ margin: 2 }} elevation={2}>
            <CardContent sx={{ display: "flex", flexDirection: "column", gap: 2 }}>
                <Box sx={{ display: "flex", justifyContent: "space-between" }}>
                    <Typography variant="h5">#{game.id}</Typography>
                    <Chip label={`${game.status}`} variant="outlined" />
                </Box>
                <Divider />
                <TableContainer>
                    <Table>
                        <TableBody>
                            <TableRow>
                                <TableCell>Created</TableCell>
                                <TableCell>{date(game.createdAt)}</TableCell>
                            </TableRow>
                            {game.status === GameStatus.Created && <>
                                <TableRow>
                                    <TableCell>Player A</TableCell>
                                    <TableCell>{game.boards[Player.A] !== undefined ? "Joined" : "Hasn't joined yet"}</TableCell>
                                </TableRow>
                                <TableRow>
                                    <TableCell>Player B</TableCell>
                                    <TableCell>{game.boards[Player.B] !== undefined ? "Joined" : "Hasn't joined yet"}</TableCell>
                                </TableRow>
                            </>}
                            {game.status === GameStatus.Started && <>
                                <TableRow>
                                    <TableCell>Started</TableCell>
                                    <TableCell>{game.startedAt && date(game.startedAt)}</TableCell>
                                </TableRow>
                                <TableRow>
                                    <TableCell>Last Action</TableCell>
                                    <TableCell>{date(game.updatedAt)}</TableCell>
                                </TableRow>
                                <TableRow>
                                    <TableCell>Turn</TableCell>
                                    <TableCell>Player {game.turn}</TableCell>
                                </TableRow>
                            </>}
                            {game.status === GameStatus.Finished && <>
                                <TableRow>
                                    <TableCell>Finished</TableCell>
                                    <TableCell>{game.finishedAt && date(game.finishedAt)}</TableCell>
                                </TableRow>
                                <TableRow>
                                    <TableCell>Winner</TableCell>
                                    <TableCell>Player {game.turn}</TableCell>
                                </TableRow>
                            </>}
                        </TableBody>
                    </Table>
                </TableContainer>
            </CardContent>
            <CardActions sx={{ display: "flex", justifyContent: "flex-end" }}>
                {game.status === GameStatus.Created && Object.keys(game.boards).length < 2 && (
                    <Button onClick={() => {
                        const player = playerToUse(game)
                        window.open(`/games/${game.id}/players/${player}`)
                    }}>Join</Button>
                )}
            </CardActions>
        </Card >
    )
}

export default GameCard
