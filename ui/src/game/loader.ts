import { LoaderFunction } from "react-router-dom";
import { api } from "../api/GameAPI";
import { Game } from "../api/Game";
import { Player } from "../api/Player";

export type GamePageLoaderData = {
    game: Game;
    player: Player;
};

export const gamePageLoader: LoaderFunction<GamePageLoaderData> = async ({ params }) => {
    const { id, player } = params;
    const parsedId = Number(id)
    if (isNaN(parsedId)) throw new Error(`Invalid game id: ${id}`);
    if (player !== Player.A && player !== Player.B) throw new Error(`Invalid player: ${player}`);
    const game = await api.get(parsedId);
    return { game, player };
};
