import axios from "axios";
import { Game } from "./Game";
import { Coordinates } from "./Coordinates";
import { Player } from "./Player";
import { Ship } from "./Ship";

interface GameAPI {
    create: () => Promise<Game>
    list: (onUpdate: (games: Game[]) => void) => () => void
    get: (id: number) => Promise<Game>
    join: (id: number, player: Player) => Promise<void>
    placeShip: (id: number, player: Player, ship: Ship) => Promise<Game>
    start: (id: number) => Promise<Game>
    shoot: (id: number, player: Player, coordinates: Coordinates) => Promise<Game>
    play: (id: number, onUpdate: (game: Game) => void) => () => void
}

const BASE_URL = import.meta.env.VITE_API_URL;

const http = axios.create({ baseURL: BASE_URL });

export const api: GameAPI = {
    create: async () => {
        const response = await http.post<Game>("/games");
        return response.data;
    },

    list: (onUpdate) => {
        const eventSource = new EventSource(`${BASE_URL}/games`);
        eventSource.onmessage = (event) => {
            const games = JSON.parse(event.data) as Game[];
            onUpdate(games);
        };
        return () => {
            eventSource.close();
        };
    },

    get: async (id) => {
        const response = await http.get<Game>(`/games/${id}`);
        return response.data;
    },

    join: async (id, player) =>
        await http.put(`/games/${id}/players/${player}`),

    placeShip: async (id, player, ship) => {
        const response = await http.post<Game>(`/games/${id}/players/${player}/ships`, ship);
        return response.data;
    },

    start: async (id) => {
        const response = await http.put<Game>(`/games/${id}`);
        return response.data;
    },

    shoot: async (id, player, coordinates) => {
        const response = await http.post<Game>(`/games/${id}/players/${player}/shots`, coordinates);
        return response.data;
    },

    play: (id, onUpdate) => {
        const eventSource = new EventSource(`${BASE_URL}/games/${id}`);
        eventSource.onmessage = (event) => {
            const game = JSON.parse(event.data) as Game;
            onUpdate(game);
        };
        return () => {
            eventSource.close();
        };
    }
}
