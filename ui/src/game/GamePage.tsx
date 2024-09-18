import { useEffect, useState } from "react";
import { Direction } from "../api/Direction";
import { api } from "../api/GameAPI";
import { GameStatus } from "../api/GameStatus";
import { useLoaderData } from "react-router-dom";
import { Player } from "../api/Player";
import Board from "./Board";
import { GamePageLoaderData } from "./loader";
import { Game } from "../api/Game";
import { ShipType, shipTypeSizes } from "../api/ShipType";
import { AxiosError } from "axios";
import { Accordion, AccordionActions, AccordionDetails, AccordionSummary, Alert, Box, Button, ButtonGroup, Card, FormControlLabel, FormGroup, Switch, Typography } from "@mui/material";

const GamePage = () => {
    const { player, ...loadedData } = useLoaderData() as GamePageLoaderData;

    const [game, setGame] = useState<Game>(loadedData.game);
    const [errorMessage, setErrorMessage] = useState<string>("");

    useEffect(() => {
        return api.play(game.id, (newGame) => {
            setGame(newGame)
            setErrorMessage("")
        });
    }, []);

    useEffect(() => {
        if (game.boards[player] === undefined) {
            api.join(game.id, player)
        }
    }, [game]);

    const [placingShips, setPlacingShips] = useState(false);
    const [selectedLocation, setSelectedLocation] = useState<number[]>([]);
    const [direction, setDirection] = useState<Direction>(Direction.Horizontal);

    const canStartGame = () =>
        Object.keys(game.boards).length === Object.keys(Player).length
        && Object.keys(game.ships).length === Object.keys(Player).length
        && !Object.values(game.ships).some((ships) => ships.length < 5)

    const otherPlayer = player === Player.A ? Player.B : Player.A;

    const startPlacingShip = (x: number, y: number) => {
        if (game.status !== GameStatus.Created) {
            return;
        }

        if ((game.ships[player] || []).length < 5) {
            setPlacingShips(true);
            setSelectedLocation([x, y]);
        } else {
            setErrorMessage("You have already placed all your ships")
        }
    }

    const placeShip = async (x: number, y: number, direction: Direction, type: ShipType) => {
        try {
            await api.placeShip(game.id, player, { x, y, direction, type })
            setPlacingShips(false)
            setSelectedLocation([-1, -1])
        } catch (error) {
            if (error instanceof AxiosError) {
                setErrorMessage(error?.response?.data?.message)
            } else {
                setErrorMessage("An error occurred")
            }
        }
    }

    const shoot = async (x: number, y: number) => {
        if (game.status !== GameStatus.Started) {
            return;
        }

        try {
            await api.shoot(game.id, player, { x, y })
        } catch (error) {
            if (error instanceof AxiosError) {
                setErrorMessage(error?.response?.data?.message)
            } else {
                setErrorMessage("An error occurred")
            }
        }
    }

    const alertMessage = (): string => {
        if (game.status === GameStatus.Created) {
            if ((game.ships[player] || []).length < 5) {
                return "You need to place all your ships."
            }

            if (game.boards[otherPlayer] === undefined) {
                return "Waiting for other player to join."
            }

            if (game.ships[otherPlayer]?.length < 5) {
                return "Waiting for other player to place all their ships."
            }
        } else if (game.status === GameStatus.Started) {
            return game.turn === player ? "Your turn" : "Opponent's turn"
        }


        return ""
    }

    const renderAlert = (): JSX.Element => {
        if (errorMessage) {
            return <Alert severity="error" sx={{ mt: 2 }}>{errorMessage}</Alert>
        }

        if (game.winner) {
            if (game.winner === player) {
                return <Alert severity="success" sx={{ mt: 2 }}>You win!</Alert>
            }

            return <Alert severity="warning" sx={{ mt: 2 }}>You lose!</Alert>
        }

        const message = alertMessage()

        return message ? <Alert severity="info" sx={{ mt: 2 }}>{message}</Alert> : <></>
    }

    return <>
        <Typography variant="h4" textAlign="center">Game #{game.id} - Player {player}</Typography>

        <Box sx={{ display: "flex", flexDirection: "row" }}>
            <Card sx={{ m: 2, flex: 1, opacity: game.status === GameStatus.Created || game.status === GameStatus.Finished || game.turn === otherPlayer ? 1 : 0.5 }}>
                <Typography variant="h4" textAlign="center">Your Board</Typography>
                <Board
                    game={game}
                    player={player}
                    isOpponent={false}
                    onClick={startPlacingShip}
                    getContent={(cell) => (cell === "A" && player !== Player.A) || (cell === "B" && player !== Player.B) ? " " : cell}
                    isSelected={(x, y) => game.status === GameStatus.Created ?
                        (x === selectedLocation[0] && y === selectedLocation[1] && game.boards[player] && game.boards[player][y][x] === " ")
                        : (game.boards[player] && (game.boards[player][y][x] === "X" || game.boards[player][y][x] === "O"))
                    } />
            </Card>
            <Card sx={{ m: 2, flex: 1, opacity: (game.status === GameStatus.Started && game.turn === player) || game.status === GameStatus.Finished ? 1 : 0.5 }}>
                <Typography variant="h4" textAlign="center">Opponent's Board</Typography>
                <Board
                    game={game}
                    player={otherPlayer}
                    isOpponent={true}
                    onClick={shoot}
                    getContent={(cell) => cell === "X" || cell === "O" ? cell : " "}
                    isSelected={(x, y) => game.boards[otherPlayer] && (game.boards[otherPlayer][y][x] === "X" || game.boards[otherPlayer][y][x] === "O")} />
            </Card>
        </Box>

        {game.status === GameStatus.Created && canStartGame() && (
            <Button variant="contained" sx={{ mt: 2 }} onClick={() => api.start(game.id)}>Start game</Button>
        )}

        {renderAlert()}

        {game.status === GameStatus.Finished && (
            <Button
                variant="contained"
                sx={{ mt: 2 }}
                onClick={() => { location.href = "/" }}>
                Back to games
            </Button>
        )}

        {game.status === GameStatus.Created && (
            <Accordion expanded={placingShips} sx={{ mt: 2 }}>
                <AccordionSummary id="place-ship-panel">
                    <Typography variant="h5">
                        {placingShips ? "Place Your Ship" : `Your Ships (${(game.ships[player] || []).length}/5)`}
                    </Typography>
                </AccordionSummary>
                <AccordionDetails sx={{ display: "flex", justifyContent: "center" }}>
                    <FormGroup>
                        <FormControlLabel
                            control={
                                <Switch
                                    checked={direction === Direction.Horizontal}
                                    onChange={() => setDirection(direction === Direction.Horizontal ? Direction.Vertical : Direction.Horizontal)} />
                            }
                            label={direction === Direction.Horizontal ? "Horizontally" : "Vertically"} />
                    </FormGroup>
                    <ButtonGroup>
                        {Object.keys(ShipType).map((type) => (
                            <Button
                                key={type}
                                variant="outlined"
                                disabled={game.ships[player]?.find((ship) => ship.type === type) !== undefined}
                                onClick={() => placeShip(selectedLocation[0], selectedLocation[1], direction, type as ShipType)}>
                                {type} ({shipTypeSizes[type as ShipType]})
                            </Button>
                        ))}
                    </ButtonGroup>
                </AccordionDetails>
                <AccordionActions>
                    <Button onClick={() => {
                        setPlacingShips(false)
                        setSelectedLocation([-1, -1])
                    }}>Cancel</Button>
                </AccordionActions>
            </Accordion>
        )}
    </>
}

export default GamePage
