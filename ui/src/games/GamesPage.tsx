import { api } from "../api/GameAPI";
import { Player } from "../api/Player";
import { Game } from "../api/Game";
import { Box, Fab, Typography } from "@mui/material";
import Grid from "@mui/material/Grid2";
import GameCard from "./GameCard";
import { useEffect, useState } from "react";

const GamesPage = () => {
    const [games, setGames] = useState<Game[]>([]);

    useEffect(() => api.list((newGames) => setGames(newGames)), []);

    return <Box display="flex" flexDirection="column">
        {(games.length === 0) ? (
            <Typography variant="h5" textAlign="center">There are no games yet.</Typography>
        ) : <>
            <Typography variant="h4" textAlign="center">Games</Typography>
            <Grid container spacing={1} size="auto" justifyContent="center">
                {games.map((game) => (<GameCard key={game.id} game={game} />))}
            </Grid>
        </>
        }
        <Fab
            variant="extended"
            color="primary"
            sx={{ position: 'fixed', bottom: 0, left: "auto", right: 0, mb: 4, mr: 4 }}
            onClick={() => api.create().then((id) => window.open(`/games/${id}/players/${Player.A}`))}>
            New game
        </Fab>
    </Box>
}

export default GamesPage
